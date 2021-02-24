//package com.newbiest.gateway.core.balancer;
//
//import com.example.demo.common.PrincessConfig;
//import com.example.demo.common.PrincessWeight;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * *平滑加权轮询
// *
// * @author sullivan
// *
// */
//public class WeightRoundRobinV3 {
//
//    // weightMap
//    private static Map<String, PrincessWeight> weightMap = new HashMap<String, PrincessWeight>();
//    // 总权重值
//    private static int totalWeight = 0;
//
//    public static void main(String[] args) {
//        for (int i = 0; i < 11; i++) {
//            System.out.println(getPrincess());
//        }
//    }
//
//    /**
//     * * 平滑加权轮询
//     * weight
//     * currentWeight
//     *
//     *
//     轮询次数 currentWeight=currentWeight+weight  max(currentWeight   return          max(currentWeight) =currentWeight-totalWeight
//            1   5,1,3,2     5   1   -6,1,3,2
//            2   -1,2,6,4    6   3   -1,2,-5,4
//            3   4,3,-2,6    6   4   4,3,-2,-5
//            4   9,4,1,-3    9   1   -2,4,1,-3
//            5   3,5,4,-1    5   2   3,-6,4,-1
//            6   8,-5,7,1    8   1   -3,-5,7,1
//            7   2,-4,10,3   10  3   2,-4,-1,3
//            8   7,-3,2,5    7   1   -4,-3,2,5
//            9   1,-2,5,7    7   4   1,-2,5,-4
//            10  6,-1,8,-2   8   3   6,-1,-3,-2
//            11  11,0,0,0    11  1   0,0,0,0
//     *
//     * @return
//     */
//    private static String getPrincess() {
//
//        //初始化map
//        if (weightMap.isEmpty()) {
//            for (String princess : PrincessConfig.PRINCESS_MAP.keySet()) {
//                // 算法的第一点：初始dynamicWeight为0
//                weightMap.put(princess, new PrincessWeight(princess, PrincessConfig.PRINCESS_MAP.get(princess), 0));
//                totalWeight += PrincessConfig.PRINCESS_MAP.get(princess);
//            }
//        }
//
//        // 算法的第二点：设置currentWeight=设置weight+currentWeight
//        for (PrincessWeight weight : weightMap.values()) {
//            weight.setDynamicWeight(weight.getWeight() + weight.getDynamicWeight());
//        }
//
//        // 算法的第三点：寻找最大的currentWeight
//        PrincessWeight maxPrincessWeight = null;
//        for (PrincessWeight weight : weightMap.values()) {
//            if (maxPrincessWeight == null || weight.getDynamicWeight() > maxPrincessWeight.getDynamicWeight()) {
//                maxPrincessWeight = weight;
//            }
//        }
//
//        // 算法的第四点：最大的dynamicWeight = dynamicWeight-totalWeight
//        maxPrincessWeight.setDynamicWeight(maxPrincessWeight.getDynamicWeight() - totalWeight);
//
//          return maxPrincessWeight.getPrincess();
//    }
//}
