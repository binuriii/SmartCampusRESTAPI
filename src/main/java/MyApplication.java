
import com.mycompany.smartcampusrestapi.resources.DiscoveryResource;
import com.mycompany.smartcampusrestapi.resources.SensorResource;
import com.mycompany.smartcampusrestapi.resources.SensorRoomResource;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author binuripiyathma
 */

@ApplicationPath("/api/v1")

public class MyApplication extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(DiscoveryResource.class);
        classes.add(SensorRoomResource.class);
        classes.add(SensorResource.class);
        
        return classes;
    }
}
    
    
    
    
