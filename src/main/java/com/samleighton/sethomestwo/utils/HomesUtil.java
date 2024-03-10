package com.samleighton.sethomestwo.utils;

import com.google.common.collect.Lists;
import com.samleighton.sethomestwo.dao.Dao;
import com.samleighton.sethomestwo.models.Home;

import java.util.List;
import java.util.UUID;

public class HomesUtil {

    public static List<String> getPlayerHomesNameOnly(Dao<Home> homesDao, UUID playerUUID){
        List<Home> playerHomes = homesDao.getAll(playerUUID);
        return Lists.transform(playerHomes, Home::getName);
    }

    public static int getPlayerHomesCount(Dao<Home> homesDao, UUID playerUUID) {
        return homesDao.getAll(playerUUID).size();
    }
}
