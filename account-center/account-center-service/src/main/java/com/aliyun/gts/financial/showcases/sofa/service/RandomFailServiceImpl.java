package com.aliyun.gts.financial.showcases.sofa.service;

import java.util.Random;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.aliyun.gts.financial.showcases.sofa.facade.api.RandomFailService;

import org.springframework.stereotype.Service;

@Service("randomFailService")
@SofaService(bindings = {@SofaServiceBinding(bindingType = "bolt")})
public class RandomFailServiceImpl implements RandomFailService{

    @Override
    public int run() {
        Random random = new Random();
        int value = random.nextInt(10);
        if (value < 8) {
            throw new RuntimeException("RandomFAIL");
        } 
        return value;
    }
    
}
