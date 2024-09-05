package com.moocsk.lowcode.youdao.translate.connector;

import java.util.ArrayList;
import java.util.List;

import com.moocsk.lowcode.youdao.translate.api.TransApi;
import com.moocsk.lowcode.youdao.translate.model.TranslateResult;
import com.moocsk.lowcode.youdao.translate.model.TranslateResultSingle;
import com.moocsk.lowcode.youdao.translate.util.ModelUtil;
import com.moocsk.lowcode.youdao.translate.util.StringUtil;
import com.netease.lowcode.core.annotation.NaslConnector;

/**
 * 百度翻译连接器
 */
@NaslConnector(connectorKind = "YoudaoTranslate")
public class YoudaoTranslate {

    /** 应用ID */
    private String appid;

    /** 密钥 */
    private String secretKey;

    /**
     * 初始化
     * 
     * @param appid     应用ID
     * @param secretKey 密钥
     * @return 百度翻译连接器
     */
    @NaslConnector.Creator
    public YoudaoTranslate init(String appid, String secretKey) {
        YoudaoTranslate baiduTranslate = new YoudaoTranslate();
        baiduTranslate.appid = appid;
        baiduTranslate.secretKey = secretKey;
        return baiduTranslate;
    }

    /**
     * 连通性测试
     * 
     * @param appid     应用ID
     * @param secretKey 密钥
     * @return 连通结果
     */
    @NaslConnector.Tester
    public Boolean testConnection(String appid, String secretKey) {
        TransApi transApi = new TransApi(appid, secretKey);
        List<String> list = new ArrayList<String>();
        list.add("测试");
        TranslateResult translateResult = transApi.generalTextTranslation(list, "zh-CHS", "en");
        if (translateResult.errorCode.equals("0")) {
            return true;
        }
        return false;
    }

    /**
     * 单条文本翻译
     * 
     * @param q    需要翻译的内容
     * @param from 源语言
     * @param to   目标语言
     * @return 单条翻译结果
     */
    @NaslConnector.Logic
    public TranslateResultSingle translation(String q, String from, String to) {
        TranslateResultSingle translateResultSingle = new TranslateResultSingle();
        // 翻译文本总长度控制在 5000 字节以内
        int qLen = StringUtil.getStringLengthByByte(q); // 翻译文本字节长度
        if (qLen > 5000) {
            translateResultSingle.setErrorCode("500");
            translateResultSingle.setErrorMsg("请将翻译内容控制在5000字节以内");
            return translateResultSingle;
        }
        TransApi transApi = new TransApi(this.appid, this.secretKey);
        List<String> qList = new ArrayList<>();
        qList.add(q);
        TranslateResult translateResult = transApi.generalTextTranslation(qList, from, to);
        translateResultSingle = ModelUtil.getSingleTranslate(translateResult);
        return translateResultSingle;
    }

    /**
     * 批量文本翻译
     * 
     * @param q    需要翻译的内容
     * @param from 源语言
     * @param to   目标语言
     * @return 批量翻译结果
     */
    @NaslConnector.Logic
    public TranslateResult translationBatch(List<String> q, String from, String to) {
        TranslateResult translateResult = new TranslateResult();
        // 批量翻译限制 List 长度为 6000
        int qSize = q.size();
        if (qSize > 5000) {
            translateResult.setErrorCode("500");
            translateResult.setErrorMsg("请将批量翻译数量控制在5000个以内");
            return translateResult;
        }
        // 批量翻译文本总长度控制在 5000 字节以内
        String qStr = String.join("", q);
        int qStrLen = StringUtil.getStringLengthByByte(qStr); // 翻译文本字节长度
        if (qStrLen > 5000) {
            translateResult.setErrorCode("500");
            translateResult.setErrorMsg("请将翻译内容控制在5000字节以内");
            return translateResult;
        }
        TransApi transApi = new TransApi(this.appid, this.secretKey);
        translateResult = transApi.generalTextTranslation(q, from, to);
        return translateResult;
    }

    public static void main(String[] args) {
        String appid = "";
        String secretKey = "";
        String from = "zh-CHS";
        String to = "en";

        YoudaoTranslate baiduTranslate = new YoudaoTranslate().init(appid, secretKey);

        // 测试连通性
        Boolean connBoolean = baiduTranslate.testConnection(appid, secretKey);
        if (connBoolean) {
            System.out.println("连接成功");
        } else {
            System.out.println("连接失败");
        }

        // 测试批量文本翻译
        List<String> list = new ArrayList<String>();
        list.add("水果");
        list.add("香蕉");
        TranslateResult translateResult = baiduTranslate.translationBatch(list, from, to);
        System.out.println("批量文本翻译结果");
        System.out.println(translateResult);

        // 测试单条文本翻译
        TranslateResultSingle translateResultSingle = baiduTranslate.translation("水果", from, to);
        System.out.println("单条文本翻译结果");
        System.out.println(translateResultSingle);
    }

}
