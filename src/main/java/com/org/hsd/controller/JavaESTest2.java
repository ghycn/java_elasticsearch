package com.org.hsd.controller;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;

public class JavaESTest2 {

    private TransportClient client;

    /**
     * 获取client对象
     */
    @Before
    public void testBefore() {
        System.out.println(123);
    }

    /**
     * 测试查询
     */
    @Test
    public void testSearch() {
        System.out.println(111);
    }

}
