import com.sun.tools.internal.xjc.model.Model;
import dao.HikesDao;
import dao.LocationsDao;
import dao.Sql2oHikesDao;
import dao.Sql2oLocationsDao;
import org.sql2o.Sql2o;

import models.Hikes;
import models.Locations;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;


public class App {
    public static void main(String[] args) {
        staticFileLocation("/public");
        String connectionString = "jdbc:h2:~/todolist.db;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        Sql2oLocationsDao locationsDao = new Sql2oLocationsDao(sql2o);
        Sql2oHikesDao hikesDao = new Sql2oHikesDao(sql2o);
        // get: delete all tasks
        get("/hikes/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            hikesDao.clearAllHikes();
            res.redirect("/");
            return null;
        });
        get("/locations/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            locationsDao.clearAllLocations();
            res.redirect("/");
            return null;
        });
        //get: Get ALL instances of objects
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Locations> locations = locationsDao.getAllLocations();
            List<Hikes> hikes = hikesDao.getAll();
            model.put("locations", locations);
            model.put("hikes", hikes);
            return new ModelAndView(model, "index.hbs");
        }, new  HandlebarsTemplateEngine());

        get ("/hikes/new", (request, response) -> {
            Map<String,Object> model = new HashMap<>();
            List<Locations> locations = locationsDao.getAllLocations();
            List<Hikes> hikes = hikesDao.getAll();
            model.put("availableLocations", locations);
            model.put("hikes", hikes);
            return new ModelAndView(model, "hike-input-form.hbs");
        }, new HandlebarsTemplateEngine());

        post("/hikes/new",(request, response) -> {
            Map<String, Object> model = new HashMap<>();
            String name = request.queryParams("hikeName");
            String location = request.queryParams("hikeLocation");
            String notes = request.queryParams("hikeNotes");
            int rating = Integer.parseInt(request.queryParams("hikeRating"));
            Hikes newHike = new Hikes(name, location, notes, rating, 1);
            hikesDao.add(newHike);
            model.put("newHike", newHike);
            response.redirect("/");
            return null;
        });

        get ("/locations/new", (request, response) -> {
            Map<String,Object> model = new HashMap<>();
            return new ModelAndView(model, "locations-input-form.hbs");
        }, new HandlebarsTemplateEngine());

        post("/locations/new",(request, response) -> {
            Map<String, Object> model = new HashMap<>();
            String state = request.queryParams("locationState");
            Locations newLocation = new Locations(state);
            locationsDao.add(newLocation);
            model.put("newLocation", newLocation);
            return new ModelAndView(model, "locations-detail.hbs");
        }, new HandlebarsTemplateEngine());

        get("/hikes/:hike_id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfHikeToFind = Integer.parseInt(req.params("hike_id"));
            Hikes newHikes = hikesDao.findById(idOfHikeToFind);
            model.put("hikes" , newHikes);
            return new ModelAndView(model, "hike-detail.hbs");
        }, new HandlebarsTemplateEngine());

        get("/hikes/:hike_id/update", (req, res) ->{
            Map<String, Object> model = new HashMap<>();
            List<Locations> locations = locationsDao.getAllLocations();
            int idOfHikeToUpdate = Integer.parseInt(req.params("hike_id"));

            model.put("availableLocations", locations);
            Hikes editHike = hikesDao.findById(idOfHikeToUpdate);
            model.put("editHike", editHike);
            return new ModelAndView(model, "hike-input-form.hbs");
        }, new HandlebarsTemplateEngine());
        post("/hikes/:hike_id/update",(request, response) -> {
            Map<String,Object>model = new HashMap<>();
            int idOfHikeToUpdate = Integer.parseInt(request.params("hike_id"));
            Hikes editHike = hikesDao.findById(idOfHikeToUpdate);
            String name = request.queryParams("hikeName");
            String location = request.queryParams("hikeLocation");
            String notes = request.queryParams("hikeNotes");
            int rating = Integer.parseInt(request.queryParams("hikeRating"));
            hikesDao.update(name,location, notes, rating,idOfHikeToUpdate, 1);
            response.redirect("/");
            return null;
        });
        get("/locations/:location_id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfLocationToFind = Integer.parseInt(req.params("location_id"));
            Locations newLocations = locationsDao.findById(idOfLocationToFind);
            model.put("locations", newLocations);
            return new ModelAndView(model, "locations-detail.hbs");
        }, new HandlebarsTemplateEngine());
        post("/locations/:location_id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int locationList = Integer.parseInt(req.params("location_id"));
            Locations locations = locationsDao.findById(locationList);
            String location = req.queryParams("locationsState");
            locationsDao.add(locations);
            model.put("locations", location);

            res.redirect("/");
            return null;
        });



    }
}




