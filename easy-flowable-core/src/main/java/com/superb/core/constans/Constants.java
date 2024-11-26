package com.superb.core.constans;

/**
 * @since 1.0  2024-10-25-9:19
 * @author MoJie
 */
public class Constants {

    /**
     * 易流程00简单的flowable
     */
    public static final String EASY_FLOWABLE = "easy-flowable";

    /**
     * 条件类型
     */
    public final static String SEQUENCE_FLOW = "sequenceFlow";
    /**
     * 用户任务
     */
    public final static String USER_TASK = "userTask";
    /**
     * 网关
     */
    public final static String GATEWAY = "Gateway";

    /**
     * 事件
     */
    public final static String EVENT = "Event";

    /**
     * 流程发起人-流程变量默认
     */
    public final static String INITIATOR = "initiator";

    /**
     * 文件
     */
    public final static String FILE = "file";

    /**
     * 流程委托状态
     */
    public final static String PENDING = "PENDING";

    /**
     * 组件banner
     */
    public final static String BANNER =
            " ______                       ________                    __    __  \n" +
            "/ ____/___ ________  __      / ____/ /___ _      ______ _/ /_  / /__\n" +
            "/ __/ / __ `/ ___/ / / /_____/ /_  / / __ \\ | /| / / __ `/ __ \\/ / _ \\\n" +
            "/ /___/ /_/ (__  ) /_/ /_____/ __/ / / /_/ / |/ |/ / /_/ / /_/ / /  __/\n" +
            "/_____/\\__,_/____/\\__, /     /_/   /_/\\____/|__/|__/\\__,_/_.___/_/\\___/\n" +
            "                 /____/                                                \n" +
            "         |---- https://www.easy-flowable.online ----|";

    /**
     * 访问路径
     */
    public static final String BASE_URL = "/easy-flowable/**";

    /**
     * 排除接口
     */
    public static final String[] IGNORE_URLS = { "/easy-flowable/index.html",
            "/easy-flowable/login", "/easy-flowable/favicon.ico", "/easy-flowable/*.css",
            "/easy-flowable/*.js", "/easy-flowable/static/**" };


    /**
     * @param name 流程名称
     * @return {@link String} 流程定义
     * @author MoJie
     * @since 1.0  2024/10/26 17:07
     */
    public static String BPMN20_XML(final String name) {
        return EASY_FLOWABLE + "-" + name + "-bpmn20.xml";
    }

    /**
     * @param name 流程名称
     * @param key 流程标识
     * @return {@link String} 流程图
     * @author MoJie
     * @since 1.0  2024/10/26 17:07
     */
    public static String DIAGRAM_PNG(final String name, final String key) {
        return EASY_FLOWABLE + "-" + name + "-" + key + ".png";
    }
}
