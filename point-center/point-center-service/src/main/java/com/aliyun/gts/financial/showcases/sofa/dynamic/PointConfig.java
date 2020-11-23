package com.aliyun.gts.financial.showcases.sofa.dynamic;

import com.alipay.drm.client.DRMClient;
import com.alipay.drm.client.api.annotation.DAttribute;
import com.alipay.drm.client.api.annotation.DObject;
import com.alipay.drm.client.api.model.DependencyLevel;

import org.springframework.beans.factory.annotation.Value;

// 动态配置演示：1. 定义动态配置类和目标参数
// 参数值的加载支持多种方式：
// NONE	  无依赖，启动期不加载服务端值，启动此级别后，客户端仅会接收在运行期间服务端产生的配置推送。
// ASYNC  异步更新，启动期异步加载服务端值，不关注加载结果。
// WEAK	  弱依赖，启动期同步加载服务端推送值，当服务端不可用时不影响应用正常启动；服务端可用后，客户端会依靠心跳检测重新拉取到服务端值。
// STRONG 强依赖，启动期同步加载服务端值，如服务端未设置值则使用代码初始化值，如从服务端获取数据请求异常或客户端设值异常时均会抛出异常，应用启动失败。
// EAGER  最强依赖，启动期必须拉取到服务端值，如服务端未推送过值则抛异常，应用启动失败。
@DObject(region = "showcase", appName = "point-center", id = "com.aliyun.gts.financial.showcases.sofa.dynamic.PointConfig")
public class PointConfig {
    
    // @DAttribute(dependency = DependencyLevel.NONE)
    @DAttribute
    @Value("${dynamic.point.value}")
    private double pointValue;

    public void init() {
        DRMClient.getInstance().register(this);
    }

    public double getPointValue() {
        return pointValue;
    }

    public void setPointValue(double pointValue) {
        this.pointValue = pointValue;
    }
}