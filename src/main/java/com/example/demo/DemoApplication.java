package com.example.demo;

import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.apollo.ApolloDataSource;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;

@SpringBootApplication
public class DemoApplication {
	
	@Value("${testprop}")
	private String testprop;

	public static void main(String[] args) {
		//解析调用来源，如果需要针对来源做限流，或者黑白名单访问控制，都需要用到origin
		WebCallbackManager.setRequestOriginParser(new RequestOriginParser() {
			@Override
			public String parseOrigin(HttpServletRequest request) {
				return request.getHeader("host") == null ? "" : request.getHeader("host");
			}
		});
		SpringApplication.run(DemoApplication.class, args);
	}
	
//	@Bean
//	public ReadableDataSource<String, List<FlowRule>> test() {
//		ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = 
//				new ApolloDataSource<>("application", "boot-demo", null, source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {}));
//		FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
//		return flowRuleDataSource;
//	}

	@RestController
	public class TestController {

		@GetMapping(value = "/hello")
//		@SentinelResource(value="hello")
		public String hello() {
			return "Hello Sentinel from "+testprop;
		}
		
		@GetMapping(value = "/hello2")
		public String hello2() {
			int sleepTime = (int)(Math.random()*1000);
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "Hello2 Sentinel sleepTime: "+ sleepTime;
		}
	}
	
//	@ApolloConfigChangeListener
//    private void configChangeListter(ConfigChangeEvent changeEvent) {
//
//        for (String changedKey : changeEvent.changedKeys()) {
//
//            if (changedKey.equals("boot-demo-flow-rules")) {
//
//                ConfigChange configChange = changeEvent.getChange(changedKey);
//                String oldValue = configChange.getOldValue();
//                String newValue = configChange.getNewValue();
//                System.out.println("newValue:"+newValue);
//                List<FlowRule> flowRules = JSON.parseObject(newValue, new TypeReference<List<FlowRule>>() {});
//                FlowRuleManager.loadRules(flowRules);
//            }
//
//        }
//    }
}
