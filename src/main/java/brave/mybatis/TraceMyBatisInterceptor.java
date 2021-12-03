package brave.mybatis;

import brave.Span;
import brave.mybatis.utils.gson.GsonUtils;
import brave.propagation.ThreadLocalSpan;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
* @Description:    java类作用描述
*
* @Author:         Joker
* @CreateDate:     2019/12/21 10:46
* @UpdateUser:     Joker
* @UpdateDate:     2019/12/21 10:46
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
@Intercepts({@Signature(
        type = Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class}
), @Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}
), @Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
)})
@Slf4j
public class TraceMyBatisInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraceMyBatisInterceptor.class);




    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        preProcess(invocation);

        Object proceed = null;
        Throwable runEx = null;
        try {
            proceed = invocation.proceed();
            return proceed;
        } catch (Throwable ex) {
            runEx = ex;
            throw ex;
        } finally {
            afterProcess(proceed,runEx);
        }
    }

    private void afterProcess(Object proceed,Throwable ex) {
        try {

            Span span = ThreadLocalSpan.CURRENT_TRACER.remove();
            if (span == null || span.isNoop()) {
                return;
            }

            if (proceed != null) {
                span.tag("result", GsonUtils.toJsonString(proceed));
            }

            if (ex != null) {
                span.tag("error", ex.toString() +"--" + ex.getCause());
            }
            span.finish();
        } catch (Exception e) {
            LOGGER.error("TraceMyBatisInterceptor afterProcess ex:{}", ex.getMessage());

        }
    }

    private void preProcess(Invocation invocation) {
        try {

            // Gets the next span (and places it in scope) so code between here and postProcess can read it
            Span span = ThreadLocalSpan.CURRENT_TRACER.next();
            if (span == null || span.isNoop()) {
                return;
            }

            Object target = invocation.getTarget();
            if (target instanceof Executor) {
                Object[] args = invocation.getArgs();


                if (args != null && args.length > 0 && args[0] instanceof MappedStatement) {
                    MappedStatement statement = (MappedStatement) args[0];


                    Object parameterObject = null;
                    if (invocation.getArgs().length > 1) {
                        parameterObject = invocation.getArgs()[1];
                    }

                    String mapper = statement.getId();
                    int lastIndexOf = mapper.lastIndexOf(".");
                    mapper = mapper.substring(lastIndexOf - 1, mapper.length() - 1);
                    String sql = statement.getBoundSql(parameterObject).getSql();
                    String paramStr = args[1] == null ? "null" : GsonUtils.toJsonString(args[1]);

                    span.kind(Span.Kind.CLIENT).name(mapper);
                    span.tag("sql.query", "sql :" + sql + "\n\r" + "param:" + paramStr);
                    span.start();
                }

            }
        } catch (Exception ex) {
            LOGGER.error("TraceMyBatisInterceptor preProcess ex:{}", ex.getMessage());
        }
    }

}