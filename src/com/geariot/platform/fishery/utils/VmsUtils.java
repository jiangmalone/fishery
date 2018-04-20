package com.geariot.platform.fishery.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dyvmsapi.model.v20170525.IvrCallRequest;
import com.aliyuncs.dyvmsapi.model.v20170525.IvrCallRequest.MenuKeyMap;
import com.aliyuncs.dyvmsapi.model.v20170525.IvrCallResponse;
import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByTtsRequest;
import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByTtsResponse;
import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByVoiceRequest;
import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByVoiceResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created on 17/6/10.
 * 语音API产品的DEMO程序,工程中包含了一个VmsDemo类，直接通过
 * 执行main函数即可体验语音产品API功能(只需要将AK替换成开通了云通信-语音产品功能的AK即可)
 * 工程依赖了2个jar包(存放在工程的libs目录下)
 * 1:aliyun-java-sdk-core.jar
 * 2:aliyun-java-sdk-dyvmsapi.jar
 *
 * 备注:Demo工程编码采用UTF-8
 */
public class VmsUtils {

    //产品名称:云通信语音API产品,开发者无需替换
    static final String product = "Dyvmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dyvmsapi.aliyuncs.com";

    //TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    static final String accessKeyId = "LTAIn3g4DqQFIuVP";
    static final String accessKeySecret = "nt5XbfunKpwwv9M5U1Moy0v7JSl3DQ";

    /**
     * 文本转语音外呼
     * @return
     * @throws ClientException
     */
    public static SingleCallByTtsResponse singleCallByTts(String phone,String tts_code,String param) throws ClientException{

        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
	    DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        SingleCallByTtsRequest request = new SingleCallByTtsRequest();
        //必填-被叫显号,可在语音控制台中找到所购买的显号
        request.setCalledShowNumber("02566806690");
        //必填-被叫号码
        request.setCalledNumber(phone);
        //必填-Tts模板ID
        request.setTtsCode(tts_code);
        //可选-当模板中存在变量时需要设置此值
        request.setTtsParam(param);
        //可选-外部扩展字段,此ID将在回执消息中带回给调用方
        //request.setOutId("yourOutId");
        
        //hint 此处可能会抛出异常，注意catch
        SingleCallByTtsResponse singleCallByTtsResponse = null;
	    singleCallByTtsResponse = acsClient.getAcsResponse(request);
        return singleCallByTtsResponse;

    }
  
    /*public static void main(String[] args) throws ClientException, InterruptedException {
        SingleCallByTtsResponse singleCallByTtsResponse = singleCallByTts("15852625983","TTS_126781509","{\"deviceName\":\"一体机\",\"way\":\"1\"}");
        System.out.println("文本转语音外呼---------------");
        System.out.println("RequestId=" + singleCallByTtsResponse.getRequestId());
        System.out.println("Code=" + singleCallByTtsResponse.getCode());
        System.out.println("Message=" + singleCallByTtsResponse.getMessage());
        System.out.println("CallId=" + singleCallByTtsResponse.getCallId());
    
    }*/

}
