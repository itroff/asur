package com.asur.asur.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.asur.asur.models.Robot;
import com.asur.asur.models.RobotStatusEnum;


//@Component
public class Robots {

    private static List<Robot> robots;
    static {
        robots = new ArrayList<>();
        robots.add(new Robot(1, "Spot", "/spot", RobotStatusEnum.NO_LINK));
        robots.add(new Robot(2, "Bender", "/bender", RobotStatusEnum.ONLINE));
        robots.add(new Robot(3, "Wall-E", "/walle", RobotStatusEnum.FAULT));

    }

    public static List<Robot> getRobots() {
        return robots;
    } 

    public static Robot getRobot(int id) {
        for (Robot robot : robots) {
            if (robot.getId() == id) {
                return robot;
            }
        }
        return null;
    }
}
