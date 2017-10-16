package au.com.woolworths.core.schedulers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.woolworths.constants.WoolworthsConstant;

@Component(immediate=true,metatype = true, label = "Weather Scheduler", 
description = "Weather scheduler")
@Service(value = Runnable.class)
@Properties({
	@Property( name = "scheduler.period", longValue = 900),
@Property(name = "scheduler.concurrent", boolValue=false,
    description = "Whether or not to schedule this task concurrently")
})
public class WeatherScheduler implements Runnable {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	String high=null,low=null;;
	@Reference
    private ResourceResolverFactory resourceResolverFactory=null;	
	private Session session=null;	
	public JSONObject getWeatherData(){
		
		JSONObject json=new JSONObject();
		try{
		if(high!=null && low!=null){
			json.put(WoolworthsConstant.HIGH, high);
			json.put(WoolworthsConstant.LOW, low);
		}
		}catch(Exception e){e.printStackTrace();}
		return json;
}
	@Override
	public void run() {
		String output=null;
		JSONObject json=null,temperatureJson=null;
		JSONArray array=null;
		ResourceResolver adminResourceResolver = null;
		Resource resource=null;		
		try {
			adminResourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
			URL url = new URL("https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22sydney%2C%20nsw%22)%20and%20u%3D%22c%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			while ((output = br.readLine()) != null) {
					json=new JSONObject(output);					
					array=json.getJSONObject(WoolworthsConstant.QUERY).getJSONObject(WoolworthsConstant.RESULTS).getJSONObject(WoolworthsConstant.CHANNEL).getJSONObject(WoolworthsConstant.ITEM).getJSONArray(WoolworthsConstant.FORECAST);
					log.info(array.get(0).toString());					
					temperatureJson=array.getJSONObject(0);
					high=temperatureJson.getString(WoolworthsConstant.HIGH);
					low=temperatureJson.getString(WoolworthsConstant.LOW);
					log.info("high>>"+high+" Low>>"+low);
			}
			
			if(adminResourceResolver !=null){log.info("adminResourceResolver isnot null");
				resource=adminResourceResolver.getResource(WoolworthsConstant.WEATHER_NODE);
				session=adminResourceResolver.adaptTo(Session.class);
				Node node=resource.adaptTo(Node.class);				
				if(node.hasProperty("jcr:primaryType"))log.info(node.getProperty("jcr:primaryType").getString());
				
				node.setProperty(WoolworthsConstant.HIGH, high);
				node.setProperty(WoolworthsConstant.LOW, low);	
				session.save();
			}
			

			conn.disconnect();
		} catch (Exception e) {
			log.info("Error inside run method" + e.getMessage());
			e.printStackTrace();
		}

	}

}
