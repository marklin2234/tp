package fittrack.parser;

import fittrack.UserProfile;
import fittrack.command.AddMealCommand;
import fittrack.command.AddWorkoutCommand;
import fittrack.command.BmiCommand;
import fittrack.command.CaloriesConsumedCommand;
import fittrack.command.CaloriesBurntCommand;
import fittrack.command.CheckRecommendedWeightCommand;
import fittrack.command.Command;
import fittrack.command.CommandResult;
import fittrack.command.DeleteMealCommand;
import fittrack.command.DeleteWorkoutCommand;
import fittrack.command.EditProfileCommand;
import fittrack.command.ExitCommand;
import fittrack.command.HelpCommand;
import fittrack.command.InvalidCommand;
import fittrack.command.ViewMealCommand;
import fittrack.command.ViewProfileCommand;
import fittrack.command.ViewWorkoutCommand;
import fittrack.command.FindMealCommand;
import fittrack.command.FindWorkoutCommand;
import fittrack.data.Gender;
import fittrack.data.Meal;
import fittrack.data.Weight;
import fittrack.data.Height;
import fittrack.data.Calories;
import fittrack.data.Workout;
import fittrack.data.Date;


import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the parser which parses commands.
 * <p>
 * Referenced
 * <a href="https://github.com/se-edu/addressbook-level2/blob/master/src/seedu/addressbook/parser/Parser.java">here</a>
 * to build main structure of this class.
 */
public class CommandParser {
    // This constant has to be changed whenever any command is added.
    public static final String ALL_COMMAND_WORDS = "help, exit,\n" +
            "editprofile, viewprofile, bmi, checkrecommendedweight,\n" +
            "addmeal, deletemeal, viewmeal, findmeal, caloriesconsumed,\n" +
            "addworkout, deleteworkout, viewworkout, findworkout, caloriesburnt";

    private static final String WORD_CG = "word";
    private static final String ARGS_CG = "args";
    private static final String HEIGHT_CG = "height";
    private static final String WEIGHT_CG = "weight";
    private static final String GENDER_CG = "gender";
    private static final String CAL_LIMIT_CG = "calLimit";
    private static final String NAME_CG = "name";
    private static final String CALORIES_CG = "calories";
    private static final String DATE_CG = "date";

    private static final Pattern COMMAND_PATTERN = Pattern.compile(
            "(?<" + WORD_CG + ">\\S+)(?<" + ARGS_CG + ">.*)"
    );
    private static final Pattern PROFILE_PATTERN = Pattern.compile(
            "h/(?<" + HEIGHT_CG + ">\\S+)\\s+w/(?<" + WEIGHT_CG +
                    ">\\S+)\\s+g/(?<" + GENDER_CG + ">\\S+)\\s+l/(?<" + CAL_LIMIT_CG + ">\\S+)"
    );
    private static final Pattern MEAL_PATTERN = Pattern.compile(
            "(?<" + NAME_CG + ">.+)\\s+c/(?<" + CALORIES_CG + ">\\S+)(\\s+d/(?<" + DATE_CG + ">\\S+))?"
    );
    private static final Pattern WORKOUT_PATTERN = Pattern.compile(
            "(?<" + NAME_CG + ">.+)\\s+c/(?<" + CALORIES_CG + ">\\S+)(\\s+d/(?<" + DATE_CG + ">\\S+))?"
    );

    public Command parseCommand(String userCommandLine) {

        final Matcher matcher = COMMAND_PATTERN.matcher(userCommandLine.strip());
        if (!matcher.matches()) {
            return getInvalidCommand(userCommandLine);
        }

        final String word = matcher.group(WORD_CG).strip();
        final String args = matcher.group(ARGS_CG).strip();

        Command command = getBlankCommand(word, userCommandLine);
        if (command instanceof InvalidCommand) {
            return getInvalidCommand(userCommandLine);
        }
        try {
            command.setArguments(args, this);
        } catch (ParseException e) {
            return getInvalidCommand(userCommandLine, e);
        }

        return command;
    }

    public Command getBlankCommand(String word, String commandLine) {
        switch (word) {
        case HelpCommand.COMMAND_WORD:
            return new HelpCommand(commandLine);
        case ExitCommand.COMMAND_WORD:
            return new ExitCommand(commandLine);
        case EditProfileCommand.COMMAND_WORD:
            return new EditProfileCommand(commandLine);
        case ViewProfileCommand.COMMAND_WORD:
            return new ViewProfileCommand(commandLine);
        case AddMealCommand.COMMAND_WORD:
            return new AddMealCommand(commandLine);
        case DeleteMealCommand.COMMAND_WORD:
            return new DeleteMealCommand(commandLine);
        case ViewMealCommand.COMMAND_WORD:
            return new ViewMealCommand(commandLine);
        case AddWorkoutCommand.COMMAND_WORD:
            return new AddWorkoutCommand(commandLine);
        case DeleteWorkoutCommand.COMMAND_WORD:
            return new DeleteWorkoutCommand(commandLine);
        case ViewWorkoutCommand.COMMAND_WORD:
            return new ViewWorkoutCommand(commandLine);
        case BmiCommand.COMMAND_WORD:
            return new BmiCommand(commandLine);
        case CaloriesConsumedCommand.COMMAND_WORD:
            return new CaloriesConsumedCommand(commandLine);
        case CheckRecommendedWeightCommand.COMMAND_WORD:
            return new CheckRecommendedWeightCommand(commandLine);
        case CaloriesBurntCommand.COMMAND_WORD:
            return new CaloriesBurntCommand(commandLine);
        case FindMealCommand.COMMAND_WORD:
            return new FindMealCommand(commandLine);
        case FindWorkoutCommand.COMMAND_WORD:
            return new FindWorkoutCommand(commandLine);
        default:
            return new InvalidCommand(commandLine);

        }
    }

    public InvalidCommand getInvalidCommand(String userCommandLine) {
        return getInvalidCommand(userCommandLine, null);
    }

    public InvalidCommand getInvalidCommand(String userCommandLine, ParseException e) {
        InvalidCommand invalidCommand = new InvalidCommand(userCommandLine, e);
        invalidCommand.setArguments(userCommandLine, this);
        return invalidCommand;
    }

    public CommandResult getInvalidCommandResult(String userCommandLine, ParseException e) {
        return getInvalidCommand(userCommandLine, e).execute();
    }

    /**
     * Parses user profile, format of `h/(HEIGHT) w/(WEIGHT) l/(CALORIES)`.
     *
     * @param profile profile as a string
     * @return height and weight as a double array
     * @throws PatternMatchFailException if regex match fails
     * @throws NumberFormatException if one of arguments is not double
     */
    public UserProfile parseProfile(String profile)
            throws PatternMatchFailException, NumberFormatException, NegativeNumberException, WrongGenderException {
        final Matcher matcher = PROFILE_PATTERN.matcher(profile);
        if (!matcher.matches()) {
            throw new PatternMatchFailException();
        }

        try {
            final double height = Double.parseDouble(matcher.group("height"));
            final double weight = Double.parseDouble(matcher.group("weight"));
            final double dailyCalorieLimit = Double.parseDouble(matcher.group("calLimit"));
            final char gender = matcher.group("gender").charAt(0);

            // Height, weight and calories cannot be negative. Throw exception if it happens
            if (height < 0 || weight < 0 || dailyCalorieLimit < 0) {
                throw new NegativeNumberException();
            }

            if (gender != 'M' && gender != 'F') {
                throw new WrongGenderException();
            }

            Height heightData = new Height(height);
            Weight weightData = new Weight(weight);
            Calories caloriesData = new Calories(dailyCalorieLimit);
            Gender genderData = new Gender(gender);

            return new UserProfile(heightData, weightData, caloriesData, genderData);
        } catch (java.lang.NumberFormatException e) {
            throw new NumberFormatException();
        }
    }

    public Meal parseMeal(String meal) throws PatternMatchFailException, NumberFormatException {
        final Matcher matcher = MEAL_PATTERN.matcher(meal);
        if (!matcher.matches()) {
            throw new PatternMatchFailException();
        }

        final String name = matcher.group(NAME_CG);
        final String calories = matcher.group(CALORIES_CG);
        final String date = matcher.group(DATE_CG);

        try {
            double caloriesInDouble = Double.parseDouble(calories);

            if (date == null) {
                return new Meal(name, new Calories(caloriesInDouble), Date.today());
            } else {
                return new Meal(name, new Calories(caloriesInDouble), new Date(date));
            }
        } catch (java.lang.NumberFormatException e) {
            throw new NumberFormatException();
        } catch (DateTimeParseException e) {
            throw new PatternMatchFailException();
        }
    }

    public Workout parseWorkout(String workout) throws PatternMatchFailException, NumberFormatException {
        final Matcher matcher = WORKOUT_PATTERN.matcher(workout);
        if (!matcher.matches()) {
            throw new PatternMatchFailException();
        }

        final String name = matcher.group(NAME_CG);
        final String calories = matcher.group(CALORIES_CG);
        final String date = matcher.group(DATE_CG);

        try {
            double caloriesInDouble = Double.parseDouble(calories);

            if (date == null) {
                return new Workout(name, new Calories(caloriesInDouble), Date.today());
            } else {
                return new Workout(name, new Calories(caloriesInDouble), new Date(date));
            }
        } catch (java.lang.NumberFormatException e) {
            throw new NumberFormatException();
        } catch (DateTimeParseException e) {
            throw new PatternMatchFailException();
        }
    }

    // @@author NgLixuanNixon
    public int parseIndex(String args) throws ParseException {
        assert args != null;
        String index = args.strip();

        if (index.isEmpty()) {
            throw new PatternMatchFailException();
        }
        try {
            int idx = Integer.parseInt(index);
            if (idx <= 0) {
                throw IndexOutOfBoundsException.INDEX_INVALID;
            }
            return idx;
        } catch (java.lang.NumberFormatException e) {
            throw new NumberFormatException("Index must be an integer.");
        }
    }
    // @@author

    // @@author NgLixuanNixon
    public Date parseDate(String args) throws PatternMatchFailException {
        assert args != null;
        String date = args.strip();

        try {
            return new Date(date);
        } catch (DateTimeParseException e) {
            throw new PatternMatchFailException();
        }
    }
    // @@author

    // @@author J0shuaLeong
    public String parseKeyword(String args) throws PatternMatchFailException {
        assert args != null;
        String keyword = args.strip();

        if (keyword.isEmpty()) {
            throw new PatternMatchFailException();
        }
        return keyword;
    }
    // @@author

    public String getFirstWord(String str) {
        assert str != null && !str.isEmpty();
        return str.split("\\s")[0];
    }
}
