package cc.mrbird.febs.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("map_vehicle")
public class Vehicle {
    public Vehicle(){}

    private Long id;

    private String name;

    private String state;

    private String loadState;

    private String position;

    private String electricLevel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLoadState() {
        return loadState;
    }

    public void setLoadState(String loadState) {
        this.loadState = loadState;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getElectricLevel() {
        return electricLevel;
    }

    public void setElectricLevel(String electricLevel) {
        this.electricLevel = electricLevel;
    }
}
