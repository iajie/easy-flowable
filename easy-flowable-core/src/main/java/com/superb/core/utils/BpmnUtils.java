package com.superb.core.utils;

import com.superb.core.exception.EasyFlowableException;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExtensionAttribute;
import org.flowable.common.engine.impl.util.io.StringStreamSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 获取任务变量
     * @param attributes 节点额外变量
     * @return {@link Map}
     * @Author: MoJie
     * @Date: 2024-10-09 13:26:35
     */
    public static Map<String, Object> getTaskAttributes(Map<String, List<ExtensionAttribute>> attributes) {
        Map<String, Object> map = new HashMap<>();
        for (String key : attributes.keySet()) {
            map.put(key, attributes.get(key).get(0).getValue());
        }
        return map;
    }
}
