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
public class EventController {
    @Autowired
    TeamService teamService;

    @Autowired
    GameService gameService;

    @Autowired
    EventService eventService;

    @Autowired
    PlayerService playerService;

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

                return "/event/registerCard";
            } else redirAttrs.addFlashAttribute("error", "Game does not exist!");

            return "redirect:/event/register";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

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

    @GetMapping("/edit/location")
    public String updateLocation(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Game> game = this.gameService.getGame(id);

            if(game.isPresent()) {
                model.addAttribute("gameForm", new GameForm());
                model.addAttribute("game", game.get());

                return "game/updateLocation";
            }

            redirAttrs.addFlashAttribute("error", "The game does not exist!");
            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/edit/location")
    public String updateLocation(@RequestParam(name="id") int id,
                                 @ModelAttribute GameForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("gameForm", form);

            Optional<Game> game = this.gameService.getGame(id);
            String location = form.getLocation();

            if(game.isPresent()) {
                boolean success = this.gameService.updateLocation(id, location);
                if(success) {
                    redirAttrs.addFlashAttribute("success", String.format("Game location changed to %s!",
                            location));
                } else redirAttrs.addFlashAttribute("error", "Failed to location!");

                return String.format("redirect:/game/profile?id=%d", id);
            }

            redirAttrs.addFlashAttribute("error", "The game does not exist!");
            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/delete")
    public String deleteEvent(@RequestParam(name = "id") int id, Model model) {
        try {
            model.addAttribute("id", id);
            return "game/delete";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/delete")
    public String deleteEventConfirm(@RequestParam(name = "id") int id, RedirectAttributes redirAttrs){
        try{
            this.eventService.deleteEvent(id);
            redirAttrs.addFlashAttribute("success", "Event deleted!");
            return "redirect:/game/list";
        } catch (Exception e) {
            redirAttrs.addFlashAttribute("error", "Failed to delete event!");
            return "redirect:/error/";
        }
    }
}
