package brave.mybatis;


import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty(value = "zipkin.brave.mybatis.enabled", havingValue = "true")
public class TraceMyBatisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    TraceMyBatisInterceptor traceMybatisInterceptor() {
        return new TraceMyBatisInterceptor();
    }



}
