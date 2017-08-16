package dao;


import models.Hikes;

import java.util.List;

public interface HikesDao {
    void add(Hikes hikes);

    List<Hikes> getAll();

    Hikes findById(int id);

    void update(String newHikeName, int id);

    void deleteById(int id);

    void clearAllHikes();

}