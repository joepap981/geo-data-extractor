package com.joepap.geodataextractor.service.shapefile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileException;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.shapefile.shp.ShapefileReader.Record;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.Geometry;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class ShapefileLoader {

//    @PostConstruct
    public void load() throws IOException {
//        File file = new File("/Users/user/Downloads/shape/Seoul_plus16_wgs84.shp");
//        FileDataStore myData = FileDataStoreFinder.getDataStore(file);
//        SimpleFeatureSource source = myData.getFeatureSource();
//        SimpleFeatureCollection featureCollection = source.getFeatures();
//        IntersectUtils.intersects
//
//        SimpleFeatureType schema = source.getSchema();
//
//        Query query = new Query(schema.getTypeName());
//        query.setMaxFeatures(1);
//
//        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(query);
//        try (FeatureIterator<SimpleFeature> features = collection.features()) {
//            while (features.hasNext()) {
//                SimpleFeature feature = features.next();
//                System.out.println(feature.getID() + ": ");
//                for (Property attribute : feature.getProperties()) {
//                    System.out.println("\t" + attribute.getName() + ":" + attribute.getValue());
//                }
//            }
//        }
    }
}
