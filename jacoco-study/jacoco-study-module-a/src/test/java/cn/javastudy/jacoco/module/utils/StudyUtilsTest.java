package cn.javastudy.jacoco.module.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StudyUtilsTest {

    @Test
    public void testSpeak() {
        try(MockedStatic<StudyUtils> utils = Mockito.mockStatic(StudyUtils.class)) {
            utils.when(StudyUtils::speak).thenReturn("123");
            Assert.assertEquals("123", StudyUtils.speak());
        }
        Assert.assertEquals("test", StudyUtils.speak());
    }
}
