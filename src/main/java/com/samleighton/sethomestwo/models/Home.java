package com.samleighton.sethomestwo.models;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Home implements Serializable {

    private String uuidBelongingTo;
    private String material;
    private String name;
    private String description;
    private String world;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    public Home(String playerUUID, String material, Location location, String name, String description){
        setUUIDBelongingTo(playerUUID);
        setMaterial(material);
        setWorld(Objects.requireNonNull(location.getWorld()).getUID().toString());
        setX(location.getX());
        setY(location.getY());
        setZ(location.getZ());
        setPitch(location.getPitch());
        setYaw(location.getYaw());
        setName(name);
        setDescription(description);
    }

    public String getUUIDBelongingTo() {
        return uuidBelongingTo;
    }

    public void setUUIDBelongingTo(String uuidBelongingTo) {
        this.uuidBelongingTo = uuidBelongingTo;
    }

    public String getMaterial() { return material; }

    public void setMaterial(String material) { this.material = material; }

    public String getWorld() { return world; }

    public void setWorld(String world) { this.world = world; }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public Location asLocation(){
        return new Location(
            Bukkit.getWorld(UUID.fromString(getWorld())),
            getX(),
            getY(),
            getZ(),
            getYaw(),
            getPitch()
        );
    }
}
