package com.panda.spring.ioc.analyze.plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wankun.pwk
 * @date 2020/4/4
 */
public class DsfCycle {

    /**
     * 限制node最大数
     */
    private static int MAX_NODE_COUNT = 100;

    /**
     * node集合
     */
    private List<String> nodes = new ArrayList<String>();

    /**
     * 有向图的邻接矩阵
     */
    private int[][] adjacencyMatrix = new int[MAX_NODE_COUNT][MAX_NODE_COUNT];

    /**
     * @param nodeName
     * @return
     * @Title addNode
     * @Description 添加节点
     * @date 2018年5月17日
     */
    private int addNode(String nodeName) {
        if (!nodes.contains(nodeName)) {
            if (nodes.size() >= MAX_NODE_COUNT) {
                System.out.println("nodes超长:" + nodeName);
                return -1;
            }
            nodes.add(nodeName);
            return nodes.size() - 1;
        }
        return nodes.indexOf(nodeName);
    }

    /**
     * @param startNode
     * @param endNode
     * @Title addLine
     * @Description 添加线，初始化邻接矩阵
     * @date 2018年5月17日
     */
    public void addLine(String startNode, String endNode) {
        int startIndex = addNode(startNode);
        int endIndex = addNode(endNode);
        if (startIndex >= 0 && endIndex >= 0) {
            adjacencyMatrix[startIndex][endIndex] = 1;
        }
    }

    /**
     * @return
     * @Title find
     * @Description 寻找闭环
     * @date 2018年5月17日
     */
    public List<String> find() {
        // 从出发节点到当前节点的轨迹
        List<Integer> trace = new ArrayList<Integer>();
        //返回值
        List<String> result = new ArrayList<>();
        if (adjacencyMatrix.length > 0) {
            findCycle(0, trace, result);
        }
        return result;
    }

    /**
     * @param v
     * @param trace
     * @param result
     * @Title findCycle
     * @Description dfs
     * @date 2018年5月17日
     */
    private void findCycle(int v, List<Integer> trace, List<String> result) {
        int j;
        //添加闭环信息
        if ((j = trace.indexOf(v)) != -1) {
            StringBuilder sb = new StringBuilder();
            String startNode = nodes.get(trace.get(j));
            while (j < trace.size()) {
                sb.append(nodes.get(trace.get(j)))
                        .append(" -> ");
                j++;
            }
            result.add("循环依赖" + (result.size() + 1) + ": " + sb.toString() + startNode);
            return;
        }
        trace.add(v);
        for (int i = 0; i < nodes.size(); i++) {
            if (adjacencyMatrix[v][i] == 1) {
                findCycle(i, trace, result);
            }
        }
        trace.remove(trace.size() - 1);
    }
}
