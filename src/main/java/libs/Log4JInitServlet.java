package libs;

import java.io.File;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

/**
 *	
 * @author Rach (Racheal Chen)
 * 
 */

public class Log4JInitServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	 public void init(ServletConfig config) throws ServletException {  

//System.out.println("----Log4JInitServlet 正在初始化 log4j日志设置信息----");  

         String log4jLocation = config.getInitParameter("log4j-properties-location");  
         ServletContext sc = config.getServletContext();  

         if (log4jLocation == null) {  

//System.err.println("----缺少 log4j-properties-location 初始化文件, 使用  BasicConfigurator 进行初始化----");  
             BasicConfigurator.configure();  
         
         } else {  
             
        	 String webAppPath = sc.getRealPath("/").replaceAll("\\\\", "/");
             String log4jProp = webAppPath + log4jLocation;  
             File file = new File(log4jProp);  
             
             if (file.exists()) {  
//System.out.println("----使用: " + log4jProp + " 初始化日志设置信息----");  
                     PropertyConfigurator.configure(log4jProp);  
             } else {  
//System.err.println("----缺少 " + log4jProp + " 文件， 使用  BasicConfigurator 进行初始化----");  
                     BasicConfigurator.configure();  
             }
             
         }  
         
         super.init(config);  
         
   }  
	 
}
