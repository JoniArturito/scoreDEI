/**
 * This class is responsible for the event registration and edition
 */
package com.scoreDEI.scoreDEI;

import com.scoreDEI.Entities.*;
import com.scoreDEI.Forms.*;
import com.scoreDEI.Services.EventService;
import com.scoreDEI.Services.GameService;
import com.scoreDEI.Services.PlayerService;
import com.scoreDEI.Services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Time;
import java.util.Optional;

@Controller
@RequestMapping("/event")
public class EventDataController {
    @Autowired
    TeamService teamService;

    @Autowired
    GameService gameService;

    @Autowired
    EventService eventService;

    @Autowired
    PlayerService playerService;

    /**
     * This function is used to register an event
     *
     * @param model This is the model that will be passed to the view.
     * @return A form to register an event.
     */
    @GetMapping("/register")
    public String registerEventForm(Model model) {
        try {
            model.addAttribute("games", this.gameService.getAllGames());
            model.addAttribute("EventForm", new EventForm());

            return "/event/register";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * It takes the form data from the user and redirects them to the appropriate page to register the event
     *
     * @param form the form that contains the information about the event
     * @return A string
     */
    @PostMapping("/register")
    public String registerEventSubmit(@ModelAttribute EventForm form) {
        try {
            switch(form.getTypeEvent()) {
                case 0:
                    return String.format("redirect:/event/register/gameStatus?name=%s&type=0", form.getGame());
                case 1:
                    return String.format("redirect:/event/register/gameStatus?name=%s&type=1", form.getGame());
                case 2:
                    return String.format("redirect:/event/register/goal?name=%s", form.getGame());
                case 3:
                    return String.format("redirect:/event/register/card?name=%s&isYellow=true", form.getGame());
                case 4:
                    return String.format("redirect:/event/register/card?name=%s&isYellow=false", form.getGame());
                case 5:
                    return String.format("redirect:/event/register/gameStatus?name=%s&type=2", form.getGame());
                case 6:
                    return String.format("redirect:/event/register/gameStatus?name=%s&type=3", form.getGame());
                default:
                    break;
            }

            return "/event/register";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * If the game exists, and the game hasn't ended, and the game has begun, and the event type is correct, then return
     * the form to register the event
     *
     * @param name the name of the game
     * @param type 0 - beginning, 1 - ending, 2 - pause, 3 - resume
     * @param model the model that will be used to render the view
     * @param redirAttrs RedirectAttributes is a Spring object that allows us to pass attributes to the next request.
     * @return A form to register a game status event.
     */
    @GetMapping("/register/gameStatus")
    public String registerGameStatusForm(@RequestParam(name="name") String name, @RequestParam(name="type") int type, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Game> opGame = this.gameService.getGame(name);
            if (opGame.isPresent()) {
                Game game = opGame.get();

                if (this.eventService.endingGameExists(game)){
                    redirAttrs.addFlashAttribute("error", "Game has already ended!");
                    return "redirect:/event/register";
                }

                String[] interval;
                if (this.eventService.beginningGameExists(game)) {
                    if (type == 0) {
                        redirAttrs.addFlashAttribute("error", "Game has already begun!");
                        return "redirect:/event/register";
                    }
                    interval = getInterval(game);

                    Optional<GameEvent> opMostRecent = eventService.getMostRecentEventOfGame(game);
                    if (opMostRecent.isPresent()){
                        GameEvent mostRecent = opMostRecent.get();
                        if (mostRecent.getTypeEvent() == 2) {
                            GameStatus mRecent = (GameStatus) mostRecent;
                            if (mRecent.getType() == 2 && type != 3){
                                redirAttrs.addFlashAttribute("error", "Failed to register event!");
                                return "redirect:/event/register";
                            }
                        }
                    }

                } else {

                    if (type == 0) {
                        interval = this.gameService.getTimeInterval(game.getBeginDate());
                    } else {
                        redirAttrs.addFlashAttribute("error", "Failed to register event!");
                        return "redirect:/event/register";
                    }
                }

                model.addAttribute("minHour", interval[0]);
                model.addAttribute("maxHour", interval[1]);
                model.addAttribute("GameStatusForm", new GameStatusForm(game, type));

                return "/event/registerGameStatus";
            } else redirAttrs.addFlashAttribute("error", "Game does not exist!");

            return "redirect:/event/register";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * It gets the beginning of the game, and then gets the most recent event time of the game, and then returns the
     * interval between the two
     *
     * @param game the game object that we want to get the events for
     * @return The method returns a String array with two elements. The first element is the time of the most recent event
     * of the game. The second element is the time of the end of the game.
     */
    private String[] getInterval(Game game) {
        String[] interval;
        Optional<Time> opBeginTime = this.eventService.getBeginningOfGame(game);
        if (opBeginTime.isPresent()) {
            Time beginTime = opBeginTime.get();
            Time maxTime = new Time(beginTime.getTime() + (long) 10800000);
            Optional<Time> opMostRecent = this.eventService.getMostRecentEventTimeOfGame(game, beginTime);
            if (opMostRecent.isPresent()) {
                Time mostRecent = opMostRecent.get();
                interval = new String[]{mostRecent.toString(), maxTime.toString()};
            }
            else{
                interval = new String[]{beginTime.toString(), maxTime.toString()};
            }
        }
        else{
            interval = this.gameService.getTimeInterval(game.getBeginDate());
        }
        return interval;
    }

    /**
     * It takes a form, checks if the game exists, and if it does, it creates a new event and adds it to the database
     *
     * @param form the form object that is used to collect the data from the form.
     * @param model The model object that is used to store data that will be used by the view.
     * @param redirAttrs RedirectAttributes is a Spring object that allows you to add attributes to the redirect.
     * @return A string
     */
    @PostMapping(value = "/register/gameStatus")
    public String registerGameStatusSubmit(@ModelAttribute GameStatusForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("GameStatusForm", form);
            form.setGameId(Integer.parseInt(form.getGameIdString()));
            form.setType(Integer.parseInt(form.getTypeString()));
            Optional<Game> opGame = this.gameService.getGame(form.getGameId());

            if (opGame.isPresent()) {
                form.setGame(opGame.get());
                Time dateAndTime = Time.valueOf(form.getEventDate());

                GameStatus dbGameStatus = new GameStatus(dateAndTime, form.getGame(), form.getType());
                this.eventService.addGameEvent(dbGameStatus);
                if (form.getType() == 1) {
                    int[] scores = gameService.getScore(form.getGame());
                    teamService.updateStatistic(form.getGame().getHomeTeam(), scores[0]-scores[1]);
                    teamService.updateStatistic(form.getGame().getVisitorTeam(), scores[1]-scores[0]);
                }

                redirAttrs.addFlashAttribute("success", "Event registered!");
            } else redirAttrs.addFlashAttribute("error", "Game does not exist!");

            return "redirect:/event/register";
        } catch (Exception e) {
            return "redirect:/error/";
        }

    }

    /**
     * If the game exists, and it hasn't ended, and it has begun, and the most recent event isn't an interruption, then show the
     * form to register a goal
     *
     * @param name the name of the game
     * @param model the model that will be used to render the view
     * @param redirAttrs RedirectAttributes is a class that allows us to add attributes to the redirect.
     * @return A form to register a goal event.
     */
    @GetMapping("/register/goal")
    public String registerGoalForm(@RequestParam(name="name") String name, Model model, RedirectAttributes redirAttrs) {
        Optional<Game> opGame = this.gameService.getGame(name);
        if (opGame.isPresent()) {
            Game game = opGame.get();

            if (this.eventService.endingGameExists(game)){
                redirAttrs.addFlashAttribute("error", "Game has already ended!");
                return "redirect:/event/register";
            }

            final long threeHours = ((3*60)*60)*1000;
            String[] interval;
            if (this.eventService.beginningGameExists(game)) {
                Optional<Time> opBeginTime = this.eventService.getBeginningOfGame(game);
                if (opBeginTime.isPresent()) {

                    Time beginTime = opBeginTime.get();
                    Time maxTime = new Time(beginTime.getTime() + threeHours);

                    Optional<Time> opMostRecent = this.eventService.getMostRecentEventTimeOfGame(game, beginTime);
                    if (opMostRecent.isPresent()) {
                        Time mostRecent = opMostRecent.get();
                        interval = new String[]{mostRecent.toString(), maxTime.toString()};

                    } else {
                        interval = new String[]{beginTime.toString(), maxTime.toString()};
                    }

                } else {
                    interval = this.gameService.getTimeInterval(game.getBeginDate());
                }
            } else {
                redirAttrs.addFlashAttribute("error", "Game hasn't begun!");

                return "redirect:/event/register";
            }

            Optional<GameEvent> opMostRecent = eventService.getMostRecentEventOfGame(game);
            if (opMostRecent.isPresent()){
                GameEvent mostRecent = opMostRecent.get();
                if (mostRecent.getTypeEvent() == 2) {
                    GameStatus mRecent = (GameStatus) mostRecent;
                    if (mRecent.getType() == 2){
                        redirAttrs.addFlashAttribute("error", "Failed to register event!");
                        return "redirect:/event/register";
                    }
                }
            }

            model.addAttribute("minHour", interval[0]);
            model.addAttribute("maxHour", interval[1]);
            model.addAttribute("GoalForm", new GoalForm(game));

            return "/event/registerGoal";
        } else redirAttrs.addFlashAttribute("error", "Game does not exist!");

        return "redirect:/event/register";
    }

    /**
     * It takes a form, checks if the game exists, checks if the player exists, checks if the player has already received a
     * red card, and if all of those are true, it registers the goal
     *
     * @param form The form object that is used to get the data from the form.
     * @param model The model is a Map of key-value pairs that will be passed to the view.
     * @param redirAttrs RedirectAttributes is a Spring object that allows you to add attributes to the redirect.
     * @return A redirect to the register page.
     */
    @PostMapping("/register/goal")
    public String registerGoalSubmit(@ModelAttribute GoalForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("GoalForm", form);
            Optional<Game> opGame = this.gameService.getGame(Integer.parseInt(form.getGameIdString()));
            if (opGame.isPresent()) {
                form.setGame(opGame.get());
                Time dateAndTime = Time.valueOf(form.getBeginDate());

                Optional<Player> opPlayer = this.playerService.getPlayer(form.getPlayerName());
                if (opPlayer.isPresent()) {
                    if(this.eventService.hasRedCard(opGame.get(), opPlayer.get())) {
                        redirAttrs.addFlashAttribute("error", String.format("Player %s has already received a red card!", opPlayer.get().getName()));
                    } else {
                        Goal dbGoal = new Goal(dateAndTime, form.getGame(), opPlayer.get());
                        this.eventService.addGoalEvent(dbGoal);
                        redirAttrs.addFlashAttribute("success", String.format("Goal from %s registered!", opPlayer.get().getName()));
                    }

                } else redirAttrs.addFlashAttribute("error", "Failed to register goal!");
            } else redirAttrs.addFlashAttribute("error", "Game does not exist!");

            return "redirect:/event/register";
        } catch (Exception e) {
            return "redirect:/error/";
        }

    }

    /**
     * It gets the game name and the card color from the request parameters, checks if the game exists, if it has already
     * ended, if it has already begun, if the last event wasn't an interruption. If
     * all of these conditions are met, it returns the registerCard view
     *
     * @param name the name of the game
     * @param isYellow boolean that indicates if the card is yellow or red
     * @param model the model that will be used to render the view
     * @param redirAttrs used to send a message to the user
     * @return A form to register a card event.
     */
    @GetMapping("/register/card")
    public String registerCardForm(@RequestParam(name="name") String name, @RequestParam(name="isYellow") boolean isYellow, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Game> opGame = this.gameService.getGame(name);
            if (opGame.isPresent()) {
                Game game = opGame.get();

                if (this.eventService.endingGameExists(game)){
                    redirAttrs.addFlashAttribute("error", "Game has already ended!");
                    return "redirect:/event/register";
                }

                String[] interval;
                if (this.eventService.beginningGameExists(game)) {
                    interval = getInterval(game);
                } else {
                    redirAttrs.addFlashAttribute("error", "Game hasn't begun!");

                    return "redirect:/event/register";
                }

                Optional<GameEvent> opMostRecent = eventService.getMostRecentEventOfGame(game);
                if (opMostRecent.isPresent()){
                    GameEvent mostRecent = opMostRecent.get();
                    if (mostRecent.getTypeEvent() == 2) {
                        GameStatus mRecent = (GameStatus) mostRecent;
                        if (mRecent.getType() == 2){
                            redirAttrs.addFlashAttribute("error", "Failed to register event!");
                            return "redirect:/event/register";
                        }
                    }
                }

                model.addAttribute("minHour", interval[0]);
                model.addAttribute("maxHour", interval[1]);
                model.addAttribute("CardForm", new CardForm(game, isYellow));
                model.addAttribute("yellow", isYellow);
                model.addAttribute("red", !isYellow);

                return "/event/registerCard";
            } else redirAttrs.addFlashAttribute("error", "Game does not exist!");

            return "redirect:/event/register";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * It registers a card event.
     *
     * @param form the form that is used to register a card event
     * @param model The model object that is used to pass data to the view.
     * @param redirAttrs RedirectAttributes is a Spring object that allows you to pass attributes to the next request.
     * @return A string
     */
    @PostMapping("/register/card")
    public String registerCardSubmit(@ModelAttribute CardForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("CardForm", form);
            Optional<Game> opGame = this.gameService.getGame(Integer.parseInt(form.getGameIdString()));
            if (opGame.isPresent()) {

                form.setGame(opGame.get());
                form.setYellow(Boolean.parseBoolean(form.getIsYellowString()));

                Time dateAndTime = Time.valueOf(form.getBeginDate());

                Optional<Player> opPlayer = this.playerService.getPlayer(form.getPlayerName());
                if (opPlayer.isPresent()) {
                    boolean feedback = this.eventService.validateCard(dateAndTime, opGame.get(), form.isYellow(), opPlayer.get());
                    if(feedback) {
                        Card dbCard = new Card(dateAndTime, form.getGame(), form.isYellow(), opPlayer.get());
                        this.eventService.addCardEvent(dbCard);
                        if(this.eventService.hasRedCard(opGame.get(), opPlayer.get())) {
                            redirAttrs.addFlashAttribute("success", String.format("Yellow and red cards appointed to %s!", opPlayer.get().getName()));
                        } else {
                            redirAttrs.addFlashAttribute("success", String.format("Yellow card appointed to %s!", opPlayer.get().getName()));
                        }
                    } else {
                        redirAttrs.addFlashAttribute("error", String.format("Player %s has received a red card!", opPlayer.get().getName()));
                    }

                } else redirAttrs.addFlashAttribute("error", "Failed to appoint card!");

            } else redirAttrs.addFlashAttribute("error", "Game does not exist!");

            return "redirect:/event/register";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }
}
