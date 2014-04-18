/*
 * Author: Sothiara Em
 * Email: sothiara@gmail.com 
 */
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;

public class Reverse_Geocode {

	private File file;
	//Feature source link to the shapefile
	private FeatureSource featureSource;
	private FeatureCollection collection;

	public Reverse_Geocode(String shapefile_path) {

		// Set up to parse the geofile
		file = new File(shapefile_path);
		try {
			Map connect = new HashMap();
			connect.put("url", file.toURL());
			int length = (int) file.length();
			DataStore dataStore = DataStoreFinder.getDataStore(connect);
			String[] typeNames = dataStore.getTypeNames();
			String typeName = typeNames[0];

			featureSource = dataStore.getFeatureSource(typeName);
			collection = featureSource.getFeatures();

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return the state code of the us based on the latitude and longitude
	 * passed in
	 * @param d
	 * @param f
	 * @return String of the state code
	 */
	public String getStateCode(double d, double f) {
		
		
		try {
			//Iterate over each state and get the state abbreviation
			FeatureIterator iterator = collection.features();
			
			while (iterator.hasNext()){
				Feature feature = iterator.next();
				if (feature.getBounds().contains(d,f)){
					return (String) ((SimpleFeature) feature).getAttribute("STATE_ABBR");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Return null if it is not in any us states
		return null;
	}
	
}
