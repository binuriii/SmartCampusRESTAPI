
import com.mycompany.smartcampus.exception.GlobalExceptionMapper;
import com.mycompany.smartcampus.exception.JsonParseExceptionMapper;
import com.mycompany.smartcampus.exception.LinkedResourceNotFoundExceptionMapper;
import com.mycompany.smartcampus.exception.NotFoundExceptionMapper;
import com.mycompany.smartcampus.exception.ResourceNotFoundExceptionMapper;
import com.mycompany.smartcampus.exception.RoomNotEmptyExceptionMapper;
import com.mycompany.smartcampus.exception.SensorUnavailableExceptionMapper;
import com.mycompany.smartcampus.filter.LoggingFilter;
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
        classes.add(RoomNotEmptyExceptionMapper.class);
        classes.add(LinkedResourceNotFoundExceptionMapper.class);
        classes.add(SensorUnavailableExceptionMapper.class);
        classes.add(GlobalExceptionMapper.class);
        classes.add(LoggingFilter.class);
        classes.add(JsonParseExceptionMapper.class);
        classes.add(ResourceNotFoundExceptionMapper.class);
        classes.add(NotFoundExceptionMapper.class);
        
        return classes;
    }
}
