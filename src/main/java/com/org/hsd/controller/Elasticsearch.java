package com.org.hsd.controller;

import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Elasticsearch {

    private IndexRequest source;
    private static TransportClient client;

    /**
     * 创建client
     * @return
     */
    @Before
    public void  getClient(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("cluster.name","elasticsearch");//设置集群名称
        map.put("xpack.security.user","elastic:123456");//设置x-pack安全认证

        Settings settings = Settings.builder().put(map).build();
        /**
         *  如果elasticsearch安装x-pack，需要进行用户认证
         *  使用PreBuiltXPackTransportClient
         *  否则PreBuiltTransportClient
         */
        client = null;
        try {
            client = new PreBuiltXPackTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.211.55.4"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭client
     */
    public void  closeClient(){
        //关闭client
        client.close();
    }

    /**
     * 查询
     */
    @Test
    public void query() {
        try {
            //搜索数据
            GetResponse response = client.prepareGet("movies", "movie", "1").execute().actionGet();
            //输出结果
            System.out.println(response.getSourceAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 查看集群信息
     */
    @Test
    public void testInfo() {
        List<DiscoveryNode> nodes = client.connectedNodes();
        for (DiscoveryNode node : nodes) {
            System.out.println(node.getHostAddress());
        }
    }

    /**
     * 存入索引中
     * @throws Exception
     */
    @Test
    public void add() throws Exception {
        JSONObject json = new JSONObject();
        json.put("user", "kimchy");
        json.put("postDate", new Date());
        json.put("message", "trying out elasticsearch");
        // 存json入索引中
        IndexResponse response = client.prepareIndex("twitter", "tweet", "1").setSource(json).get();
//        // 结果获取
        String index = response.getIndex();
        String type = response.getType();
        String id = response.getId();
        long version = response.getVersion();
        RestStatus status = response.status();
        System.out.println(index + " : " + type + ": " + id + ": " + version + ": " + status.getStatus());
    }


    /**
     * get API 获取指定文档信息
     */
    @Test
    public void testGet() {

        GetResponse response = client.prepareGet("twitter", "tweet", "1")
                .setOperationThreaded(false)    // 线程安全
                .get();
        System.out.println(response.getSourceAsString());
    }


    /**
     * 测试更新 update API
     * 使用 updateRequest 对象
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index("twitter");
        updateRequest.type("tweet");
        updateRequest.id("1");
        updateRequest.doc(XContentFactory.jsonBuilder()
                .startObject()
                // 对没有的字段添加, 对已有的字段替换
                .field("gender", "male")
                .field("message", "hello")
                .endObject());
        UpdateResponse response = client.update(updateRequest).get();

        // 打印
        String index = response.getIndex();
        String type = response.getType();
        String id = response.getId();
        long version = response.getVersion();
        System.out.println(index + " : " + type + ": " + id + ": " + version);
    }



    /**
     * 测试 delete api
     */
    @Test
    public void testDelete() {
        DeleteResponse response = client.prepareDelete("twitter", "tweet", "1")
                .get();
        String index = response.getIndex();
        String type = response.getType();
        String id = response.getId();
        long version = response.getVersion();
        System.out.println(index + " : " + type + ": " + id + ": " + version);
    }

}
