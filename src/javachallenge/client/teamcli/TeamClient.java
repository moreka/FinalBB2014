package javachallenge.client.teamcli;

import javachallenge.client.Client;
import javachallenge.exceptions.CellIsNullException;
import javachallenge.units.Unit;
import javachallenge.util.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TeamClient extends Client {

    private final int INF = 1000 * 1000 * 1000 + 10;

    // ****************************** public variable ******************************

    boolean is_started = false;

    // ****************************** start variables ******************************

    private int my_team_id = -1, en_team_id = -1;
    Cell my_spa, my_des, en_spa, en_des;
    ArrayList<MineCell> mine_list = new ArrayList<MineCell>();

    // ****************************** start variables ******************************

    private boolean is_free(Point p){
        try{
            int x = p.getX(), y = p.getY();
            if (getMap().isCellInMap(x, y))
                if (getMap().getCellAt(x, y).isGround())
                    return true;
        }catch (Exception e){ }
        return false;
    }

    private boolean is_full_free(Point p){
        try{
            int x = p.getX(), y = p.getY();
            if (getMap().isCellInMap(x, y))
                if (getMap().getCellAt(x, y).isGround())
                    if (getMap().getCellAt(x, y).getUnit() == null)
                        return true;
        }catch (Exception e){
        }
        return false;
    }

    private boolean is_unit(Point p, int team_id){
        try{
            int x = p.getX(), y = p.getY();
            if (getMap().isCellInMap(x, y))
                if (getMap().getCellAt(x, y).getUnit() != null && getMap().getCellAt(x, y).getUnit().getTeamId() == team_id)
                    return true;
        }catch (Exception e){
        }
        return false;
    }

    private boolean is_unit(Point p, Direction d, int team_id){
        try {
            return is_unit(getMap().getNeighborCell(getMap().getCellAt(p.getX(), p.getY()), d).getPoint(), team_id);
        } catch (Exception e) {
        }
        return false;
    }

    private boolean is_open(Point p, Direction d){
        try {
            if (getMap().getCellAt(p.getX(), p.getY()).getEdge(d) == null)
                return false;
            if (getMap().getCellAt(p.getX(), p.getY()).getEdge(d).getType() == EdgeType.WALL.OPEN)
                return true;
        } catch (Exception e) { }
        return false;
    }

    private boolean is_movable(Point p, Direction d) {
        if (is_open(p, d) && is_full_free(move(p, d)))
            return true;
        return false;
    }

    // ******************************* base functions ******************************

    private Direction random_direction(){
        return Direction.values()[new Random().nextInt(6)];
    }

    private Direction random_attack_direction(Cell my){
        try{
            ArrayList<Direction> list = new ArrayList<Direction>();
            for (Direction d : Direction.values())
                if (is_open(my.getPoint(), d) && is_unit(my.getPoint(), d, en_team_id))
                    list.add(d);

            if (list.size() != 0)
                return list.get(new Random().nextInt(list.size()));
        }catch (Exception e) {
        }
        return null;
    }

    private Direction random_direction(Cell c){
        Direction d = null;
        int counter = 0;
        try{
            do{
                counter++;
                d = random_direction();
            }while (counter < 20 && !is_movable(c.getPoint(), d));
        }catch (Exception e) { }
        return d;
    }

    private void random_attack(Unit unit){
        Direction d = random_attack_direction(unit.getCell());
        if (d != null)
            attack(unit, d);
    }

    private Point move(Point p, Direction d){
        try {
            Cell c = getMap().getCellAt(p.getX(), p.getY());
            return getMap().getNeighborCell(c, d).getPoint();
        } catch (Exception e) {
        }
        return null;
    }

    // ******************************* move functions ******************************

    private Direction go_to_bfs(Point str, Point des){
        try {
            Set<Point> mark = new HashSet<Point>();
            ArrayDeque<Point> Q = new ArrayDeque<Point>();
            HashMap<Point, Direction> par = new HashMap<Point, Direction>();

            Q.push(str);
            mark.add(str);

            while (Q.size() != 0){
                Point my = Q.getFirst();
                Q.pop();
                if (my.equals(des))
                    break;
                for (Direction d : Direction.values()){
                    Point next = move(my, d);
                    if (is_open(my, d) && is_free(next) && !mark.contains(next)){
                        Q.add(next);
                        mark.add(next);
                        par.put(next, !par.containsKey(my) ? d : par.get(my));

                        if (next.equals(des))
                            return par.get(des);
                    }
                }
            }

            if (par.containsKey(des))
                return par.get(des);
        } catch (Exception e) {
        }

        return null;
    }

    // ******************************* init functions ******************************

    private void Init_Static_Data(){
        try{
            my_team_id = getTeamID();
            en_team_id = 1 - getTeamID();

            my_spa = getMap().getSpawnCell(my_team_id);
            my_des = getMap().getDestinationCell(my_team_id);

            en_spa = getMap().getSpawnCell(en_team_id);
            en_des = getMap().getDestinationCell(en_team_id);
        }catch (Exception e){
        }
    }

    private void Init_Data(){
        try{
            mine_list = getMap().getMines();
        }catch (Exception e){
        }
    }

    private void Init(){
        Init_Data();

        if (is_started)
            return;
        is_started = true;

        Init_Static_Data();
    }

    // ******************************* main functions ******************************

    private void random(Unit unit){
        move(unit, random_direction(unit.getCell()));
        random_attack(unit);
    }

    private boolean is_force(){
        try {
            if (getMap().getCellAt(my_spa.getX(), my_spa.getY()).getUnit() != null)
                if (getMap().getCellAt(my_spa.getX(), my_spa.getY()).getUnit().getTeamId() == en_team_id)
                    return true;
        } catch (Exception e) { }
        return false;
    }

    private void move(Unit unit){
        Point des = my_des.getPoint();
        if (is_force())
            des = my_spa.getPoint();

        System.err.println("MOVE");
        Direction d = go_to_bfs(unit.getCell().getPoint(), my_des.getPoint());
        System.err.println("MOVE " + d + " " + unit.getId());
        if (d != null){
			/*if (is_unit(unit.getCell().getPoint(), d, en_team_id))
				attack(unit, d);
			else
				random_attack(unit);*/
            move(unit, d);
        }else{
            //random(unit);
        }
    }

    private void move_bad(Unit unit) {
        Direction d = go_to_bfs(unit.getCell().getPoint(), en_spa.getPoint());
        if (d != null){
            if (is_unit(unit.getCell().getPoint(), d, en_team_id))
                attack(unit, d);
            else
                random_attack(unit);
            move(unit, d);
        }else{
            random(unit);
        }
        random_attack(unit);
    }

    @Override
    public void step() {
        Init();

        for (Unit unit : getMyUnits()) {
            if (unit.getId() == 0)
                System.out.println(unit.isAlive() + " " + unit.isArrived());

            if (unit.isArrived() || !unit.isAlive())
                continue;

            if (my_team_id == 0)
                move(unit);
            else
                move_bad(unit);
        }
    }

    // ************************************* end ***********************************
}