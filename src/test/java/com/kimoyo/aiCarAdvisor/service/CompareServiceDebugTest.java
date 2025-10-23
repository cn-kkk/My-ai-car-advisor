package com.kimoyo.aiCarAdvisor.service;

import com.kimoyo.aiCarAdvisor.model.CarSpec;
import com.kimoyo.aiCarAdvisor.service.impl.CarSpecServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Set;

@SpringBootTest
public class CompareServiceDebugTest {

    @Autowired
    private CompareService compareService;

    @Autowired
    private CarSpecService carSpecService;

    @Test
    void printDatasetStats() throws Exception {
        System.out.println("=== 数据集/词典统计 ===");
        CarSpecServiceImpl impl = (CarSpecServiceImpl) carSpecService;
        Field fBrand = CarSpecServiceImpl.class.getDeclaredField("brandDict");
        Field fSeries = CarSpecServiceImpl.class.getDeclaredField("seriesDict");
        Field fModel = CarSpecServiceImpl.class.getDeclaredField("modelDict");
        fBrand.setAccessible(true);
        fSeries.setAccessible(true);
        fModel.setAccessible(true);
        Set<String> brands = (Set<String>) fBrand.get(impl);
        Set<String> series = (Set<String>) fSeries.get(impl);
        Set<String> models = (Set<String>) fModel.get(impl);
        System.out.println("brandDict size = " + brands.size());
        System.out.println("seriesDict size = " + series.size());
        System.out.println("modelDict size = " + models.size());
        System.out.println("-- 包含 su7 的系列条目（归一化后） --");
        series.stream().filter(s -> s.contains("su7")).limit(10).forEach(System.out::println);
        System.out.println("-- 包含 model3 的系列条目（归一化后） --");
        series.stream().filter(s -> s.contains("model3")).limit(10).forEach(System.out::println);
    }

    @Test
    void findByMessage_su7Standard() {
        System.out.println("=== findByMessage: 小米su7标准版 ===");
        Optional<CarSpec> su7 = carSpecService.findByMessage("小米su7标准版");
        if (su7.isPresent()) {
            CarSpec s = su7.get();
            System.out.println("命中: " + s.getBrand() + " " + s.getSeries() + " " + s.getModel());
            System.out.println("属性数量: " + (s.getAttributes() == null ? 0 : s.getAttributes().size()));
        } else {
            System.out.println("未命中: 小米su7标准版");
        }
    }

    @Test
    void findByMessage_model3_onlySeriesToken() {
        System.out.println("=== findByMessage: model3 (仅系列英文数字) ===");
        Optional<CarSpec> m3 = carSpecService.findByMessage("model3");
        if (m3.isPresent()) {
            CarSpec s = m3.get();
            System.out.println("命中: " + s.getBrand() + " " + s.getSeries() + " " + s.getModel());
            System.out.println("属性数量: " + (s.getAttributes() == null ? 0 : s.getAttributes().size()));
        } else {
            System.out.println("未命中: model3");
        }
    }

    @Test
    void compare_model3_vs_su7Standard() {
        System.out.println("=== compare: model3 和 小米su7标准版 ===");
        String msg = "帮我比较下model3和小米su7标准版的不同之处";
        String result = compareService.compare(msg);
        System.out.println("结果: \n" + result);
    }
}