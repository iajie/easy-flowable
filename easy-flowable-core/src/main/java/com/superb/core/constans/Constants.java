package com.superb.core.constans;

import java.util.Arrays;
import java.util.List;

/**
 * @package: {@link com.superb.core.constans}
 * @Date: 2024-10-25-9:19
 * @Description:
 * @Author: MoJie
 */
public class Constants {

    public static final String EASY_FLOWABLE = "easy-flowable";

    public final static String SEQUENCE_FLOW = "sequenceFlow";
    public final static String USER_TASK = "userTask";
    public final static String GATEWAY = "Gateway";
    public final static String EVENT = "Event";

    public final static String INITIATOR = "initiator";

    public final static String FILE = "file";

    public final static String PENDING = "PENDING";

    public final static String BANNER =
            " ______                       ________                    __    __  \n" +
            "/ ____/___ ________  __      / ____/ /___ _      ______ _/ /_  / /__\n" +
            "/ __/ / __ `/ ___/ / / /_____/ /_  / / __ \\ | /| / / __ `/ __ \\/ / _ \\\n" +
            "/ /___/ /_/ (__  ) /_/ /_____/ __/ / / /_/ / |/ |/ / /_/ / /_/ / /  __/\n" +
            "/_____/\\__,_/____/\\__, /     /_/   /_/\\____/|__/|__/\\__,_/_.___/_/\\___/\n" +
            "                 /____/                                                \n" +
            "         |---- https://www.easy-flowable.online ----|";

    public static final String BASE_URL = "/easy-flowable/**";

    public static final String[] IGNORE_URLS = { "/easy-flowable/index.html",
            "/easy-flowable/login", "/easy-flowable/favicon.ico", "/easy-flowable/*.css",
            "/easy-flowable/*.js", "/easy-flowable/static/**" };


    /**
     * @param name 流程名称
     * @return: {@link String}
     * @Author: MoJie
     * @Date: 2024/10/26 17:07
     * @Description: 流程定义
     */
    public static String BPMN20_XML(final String name) {
        return EASY_FLOWABLE + "-" + name + "-bpmn20.xml";
    }

    /**
     * @param name 流程名称
     * @param key 流程标识
     * @return: {@link String}
     * @Author: MoJie
     * @Date: 2024/10/26 17:07
     * @Description: 流程图
     */
    public static String DIAGRAM_PNG(final String name, final String key) {
        return EASY_FLOWABLE + "-" + name + "-" + key + ".png";
    }
}
