import com.ydg.context.ClassPathXmlApplicationContext;
import org.junit.Test;

/**
 * @author yudungang
 */
public class TestSpring {

    @Test
    public void testClassPathXmlApplicationContext(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext.xml");
        Object product = context.getBean("Product");
        System.out.println(product);
    }
}
