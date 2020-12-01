package com.csl.tcg;

import cn.hutool.core.util.RandomUtil;
import com.csl.tcg.core.TestCaseGenerator;
import org.junit.Test;

/**
 * @author MaoLongLong
 * @date 2020-12-01 14:58
 */
public class GeneratorTest {

    @Test
    public void testTcg() {
        new TestCaseGenerator((in, out) -> {

            final int limit = 9999;

            int rows = RandomUtil.randomInt(1, 40);
            for (int t = 0; t < rows; t++) {
                int x = RandomUtil.randomInt(limit);
                int y = RandomUtil.randomInt(limit);
                in.println(x + " " + y);
            }
        }).start();
    }

}
