package cc.mrbird.febs.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("map_point")
public class Point {
    public Point(){}

    private int id;

    private String name;

    private int xposition;

    private int yposition;

    private int zposition;

    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getXposition() {
        return xposition;
    }

    public void setXposition(int xposition) {
        this.xposition = xposition;
    }

    public int getYposition() {
        return yposition;
    }

    public void setYposition(int yposition) {
        this.yposition = yposition;
    }

    public int getZposition() {
        return zposition;
    }

    public void setZposition(int zposition) {
        this.zposition = zposition;
    }
}
