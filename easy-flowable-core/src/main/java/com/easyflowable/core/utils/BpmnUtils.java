package com.easyflowable.core.utils;

import com.easyflowable.core.exception.EasyFlowableException;
import lombok.SneakyThrows;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.impl.util.io.StringStreamSource;

/**
 * @Author: MoJie
 * @Date: 2024-11-09 11:29
 * @Description:
 */
public class BpmnUtils {

    private final static BpmnXMLConverter BPMN_XML_CONVERTER = new BpmnXMLConverter();

    /**
     * @param xml 设计器结果
     * @return: {@link BpmnModel}
     * @Author: MoJie
     * @Date: 2024/11/9 11:32
     * @Description: 根据xml字符串转bpmn对象
     */
    public static BpmnModel getBpmnModel(String xml) {
        return BPMN_XML_CONVERTER.convertToBpmnModel(new StringStreamSource(xml), false, false);
    }

    /**
     * @param xml 设计器结果
     * @return: {@link BpmnModel}
     * @Author: MoJie
     * @Date: 2024/11/9 11:32
     * @Description: 根据xml字符串转bpmn对象
     */
    public static void validateModel(String xml) {
        try {
            BPMN_XML_CONVERTER.validateModel(new StringStreamSource(xml));
        } catch (Exception e) {
            throw new EasyFlowableException("流程校验异常", e);
        }
    }

}
