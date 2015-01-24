package battleship;

import battleship.equipment.AntiAircraft;
import battleship.equipment.Equipment;
import battleship.equipment.Mine;
import battleship.equipment.Ship;
import battleship.exception.GameOverException;
import battleship.exception.NoMoreInputException;
import battleship.position.Position;

import java.io.IOException;
import java.util.ArrayList;

/** Controls the relation between GameEngine and I/O
 *
 * @author Ahanchi
 */
public class GameController {

    private GameEngine engine;
    private Log log;
    private ConsoleInput consoleInput;

    /** Initializes the engine, log and consoleInput and also sets controller and owner of all the equipments of the players
     *
     * @param engine the GameEngine
     * @param log the Log
     * @param consoleInput the consoleInput
     */
    public void init(GameEngine engine, Log log, ConsoleInput consoleInput) {
        this.engine = engine;
        this.log = log;
        this.consoleInput = consoleInput;
        for (Player player : engine.getPlayers()) {
            for (Equipment equipment : player.getMap().getEquipments()) {
                equipment.setController(this);
                equipment.setOwner(player);
            }
            player.getMap().setOwner(player);
        }

    }

    /** Starts and continues the game up to an ending point*/
    public void start() throws IOException {
        boolean gameHasEnded = false;
        while (!gameHasEnded) {
            try {
                consoleInput.next();
            } catch (GameOverException e) {
                log.println(e.getMessage());
                gameHasEnded = true;
            } catch (NoMoreInputException e) {
                gameHasEnded = true;
            }
        }
    }


    public GameEngine getEngine() {
        return engine;
    }

    /** Runs an attack command
     *
     * @param targetPosition the position which is attacked
     * @param attackingPlayer the player who has attacked
     * @throws Exception if the equipment in that position is already attacked
     */
    public void attack(Position targetPosition, Player attackingPlayer) throws GameOverException {
        engine.attack(targetPosition, attackingPlayer);
    }

    /** Runs a radar command to identify the equipments around a position
     *
     * @param targetPosition the position to perform the radar command on
     * @param attackingPlayer the player who has commanded the radar command
     */
    public void radar(Position targetPosition, Player attackingPlayer) {
        engine.radar(targetPosition, attackingPlayer);
    }

    /** Calls an aircraft attack
     *
     * @param row the row on whicj the aircraft will attack
     * @param attackingPlayer the player who has launched the aircraft
     * @throws Exception if an attacked eqipment has already been exploded
     */
    public void aircraft(int row, Player attackingPlayer) throws GameOverException {
        engine.aircraft(row, attackingPlayer);
    }

    /** Reports an explosion of cell of a ship
     *
     * @param targetPosition the position of a ship which has been exploded
     * @param attackedPlayer the player who's cell has exploded
     */
    public void reportShipCellExplode(Position targetPosition, Player attackedPlayer) {
        log.println("team " + engine.getOpponent(attackedPlayer).getName() + " explode " +  targetPosition.getString());
    }

    /** Reports complete explosion of a ship
     *
     * @param ship the ship that has been completely destroyed
     * @throws battleship.exception.GameOverException if the explosion if the ship ends the game
     */
    public void reportShipDestroyed(Ship ship) throws GameOverException {
//        log.println("ship " + ship.getName() + " destroyed");

        if (ship.getOwner().getMap().isAllShipDestroyed())
            throw new GameOverException("team " + engine.getOpponent(ship.getOwner()).getName() + " wins");
    }

    /** Reports explosion of a mine
     *
     * @param mine the mine that has been exploded
     * @throws Exception if the mines causes explosion of an already exploded cell
     */
    public void reportMineExplode(Mine mine) throws GameOverException {
        log.println("team " + engine.getOpponent(mine.getOwner()).getName() + " mine trap " + mine.getPosition().getString());
        this.attack(mine.getPosition(), mine.getOwner());
        //engine.addEvent(0, "team " + mine.getOwner().getName() + " attack " + mine.getPosition().getString());
    }

    /** Reports defence of an antiAircraft against an aircraft
     *
     * @param antiAircraft the antiAircraft that has defended against the aircraft
     */
    public void reportAntiAircraftHit(AntiAircraft antiAircraft) {
        log.println("aircraft unsuccessful");
    }

    /** Reports explosion of an antiAircraft that has benn hit directly
     *
     * @param antiAircraft the antiAircraft that has been exploded
     */
    public void reportAntiAircraftHitDirectly(AntiAircraft antiAircraft) {
        log.println("team " + antiAircraft.getOwner().getName() + " anti aircraft row " + antiAircraft.getRow() + " exploded");

    }

    /** Reports the identified positions of equipments after running a radar command
     *
     * @param positions the positions that has been identified by radar command
     * @param owner the player whose cells have been identified
     */
    public void reportRadar(ArrayList<Position> positions, Player owner) {
        for (Position position : positions)
            log.println("team " + engine.getOpponent(owner).getName() + " detected " + position.getString());
    }

}