package au.com.woolworths.core.servlets;

import java.io.IOException;

import javax.jcr.Node;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.woolworths.constants.WoolworthsConstant;

@SlingServlet(paths="/bin/apis/weather",methods="GET")
public class GetWeatherTemperatureServlet extends SlingSafeMethodsServlet {
	private static final long serialVersionUID = 1L;
	private Resource resource=null;
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		JSONObject json=null;
		JSONArray array=new JSONArray();
		String jsonString=null;
		resource = req.getResourceResolver().getResource("/content/weather");
		try{
		if(resource !=null){
			Node node=resource.adaptTo(Node.class);
			if(node.hasProperty(WoolworthsConstant.HIGH)){			 
				json=new JSONObject();
				json.put("text", WoolworthsConstant.HIGH.toUpperCase());
				json.put("value", node.getProperty(WoolworthsConstant.HIGH).getString());
				array.put(json);
			}
			
			if(node.hasProperty(WoolworthsConstant.LOW)){			 
				json=new JSONObject();
				json.put("text", WoolworthsConstant.LOW.toUpperCase());
				json.put("value", node.getProperty(WoolworthsConstant.LOW).getString());
				
				array.put(json);
				}
			jsonString=array.toString();			
		}
		}catch(Exception e){e.printStackTrace();}
		
        resp.getWriter().write(jsonString);
    }

}
