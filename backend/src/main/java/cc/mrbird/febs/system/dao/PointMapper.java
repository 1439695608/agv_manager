package cc.mrbird.febs.system.dao;

import cc.mrbird.febs.system.domain.Point;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;


public interface PointMapper extends BaseMapper<Point> {
    public int insertBatchSomeColumn(List<Point> pointList);
}
