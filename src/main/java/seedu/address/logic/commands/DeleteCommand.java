package seedu.address.logic.commands;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.ui.MainWindow;

/**
 * Deletes a person identified using it's displayed index from the address book.
 */
public class DeleteCommand extends Command {
    public static final String COMMAND_WORD = "delete";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the index number used in the displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";
    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    private final Index targetIndex;
    private final Name targetName;
    /**
     * Creates a DeleteCommand to delete the person at the specified {@code Index}.
     *
     * @param targetIndex Index of the person to delete.
     */
    public DeleteCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
        this.targetName = null;
    }
    /**
     * Creates a DeleteCommand to delete the person with the specified {@code Name}.
     *
     * @param targetName Name of the person to delete.
     */
    public DeleteCommand(Name targetName) {
        this.targetIndex = null;
        this.targetName = targetName;
    }
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();
        Person personToDelete;
        if (targetIndex != null) {
            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            personToDelete = lastShownList.get(targetIndex.getZeroBased());
        } else {
            Optional<Person> personOptional = lastShownList.stream()
                    .filter(person -> person.getName().equals(targetName))
                    .findFirst();
            if (personOptional.isEmpty()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_NAME);
            }
            personToDelete = personOptional.get();
        }

        boolean isConfirmed = MainWindow.showConfirmationDialog("Are you sure you want to delete "
                + personToDelete.getName() + "?");
        if (!isConfirmed) {
            return new CommandResult("Deletion cancelled.");
        }

        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, personToDelete));
    }
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        // instanceof handles nulls
        if (!(other instanceof DeleteCommand)) {
            return false;
        }
        DeleteCommand otherDeleteCommand = (DeleteCommand) other;
        return (targetIndex != null && targetIndex.equals(otherDeleteCommand.targetIndex))
                || (targetName != null && targetName.equals(otherDeleteCommand.targetName));
    }
    @Override
    public String toString() {
        if (targetIndex != null) {
            return getClass().getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        } else {
            return getClass().getCanonicalName() + "{targetName=" + targetName + "}";
        }
    }
}
