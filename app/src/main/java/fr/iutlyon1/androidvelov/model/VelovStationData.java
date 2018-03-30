package fr.iutlyon1.androidvelov.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

public class VelovStationData implements Serializable, ClusterItem {
    private int number;
    private String name;
    private String address;
    private LatLng position;
    private boolean banking;
    private boolean bonus;
    private String status;
    private String contractName;
    private int bikeStands;
    private int availableBikeStands;
    private int availableBikes;
    private Date lastUpdate;
    private boolean favorite;

    public VelovStationData(int number, String name, String address, LatLng position,
                            boolean banking, boolean bonus, String status, String contractName,
                            int bikeStands, int availableBikeStands, int availableBikes,
                            long lastUpdate) {
        this.number = number;
        this.name = name;
        this.address = address;
        this.position = position;
        this.banking = banking;
        this.bonus = bonus;
        this.status = status;
        this.contractName = contractName;
        this.bikeStands = bikeStands;
        this.availableBikeStands = availableBikeStands;
        this.availableBikes = availableBikes;
        this.lastUpdate = new Date(lastUpdate);
        this.favorite = false;
    }

    public boolean matches(String searchString) {
        final String fullname = this.getFullName().toUpperCase();
        searchString = searchString.toUpperCase();

        return fullname.contains(searchString);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.write(number);
        out.writeObject(name);
        out.writeObject(address);
        out.writeDouble(position.latitude);
        out.writeDouble(position.longitude);
        out.writeBoolean(banking);
        out.writeBoolean(bonus);
        out.writeObject(status);
        out.writeObject(contractName);
        out.write(bikeStands);
        out.write(availableBikeStands);
        out.write(availableBikes);
        out.writeObject(lastUpdate);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.number = in.read();
        this.name = (String) in.readObject();
        this.address = (String) in.readObject();
        double lat = in.readDouble(),
            lng = in.readDouble();
        this.position = new LatLng(lat, lng);
        this.banking = in.readBoolean();
        this.bonus = in.readBoolean();
        this.status = (String) in.readObject();
        this.contractName = (String) in.readObject();
        this.bikeStands = in.read();
        this.availableBikeStands = in.read();
        this.availableBikes = in.read();
        this.lastUpdate = (Date) in.readObject();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return number + " - " + name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public void setPosition(double lat, double lng) {
        this.position = new LatLng(lat, lng);
    }

    public boolean isBanking() {
        return banking;
    }

    public void setBanking(boolean banking) {
        this.banking = banking;
    }

    public boolean isBonus() {
        return bonus;
    }

    public void setBonus(boolean bonus) {
        this.bonus = bonus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public int getBikeStands() {
        return bikeStands;
    }

    public void setBikeStands(int bikeStands) {
        this.bikeStands = bikeStands;
    }

    public int getAvailableBikeStands() {
        return availableBikeStands;
    }

    public void setAvailableBikeStands(int availableBikeStands) {
        this.availableBikeStands = availableBikeStands;
    }

    public int getAvailableBikes() {
        return availableBikes;
    }

    public void setAvailableBikes(int availableBikes) {
        this.availableBikes = availableBikes;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String getTitle() {
        return this.getFullName();
    }

    @Override
    public String getSnippet() {
        return null;
    }

    @Override
    public String toString() {
        return this.getFullName();
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isFavorite() {
        return favorite;
    }
}
