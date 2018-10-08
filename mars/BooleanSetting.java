package mars;

import java.util.prefs.BackingStoreException;

/**
 * Last resort default values for boolean settings; will use only  if neither
 * the Preferences nor the properties file work. If you wish to change them,
 * do so before instantiating the Settings object.
 * Values are matched to keys by list position.
 */
public enum BooleanSetting {
    /**
     * Flag to determine whether or not program being assembled is limited to
     * basic MIPS instructions and formats.
     */
    /*
     * Setting for whether user programs can use pseudo-instructions or extended addressing modes
     * or alternative instruction formats (all are implemented as pseudo-instructions).
     *
     * @return true if pseudo-instructions and formats permitted, false otherwise.
     */
    /*
     * Establish setting for whether or not pseudo-instructions and formats are permitted
     * in user programs.  User can change this setting via the IDE.  If setting changes,
     * new setting will be written to properties file.
     *
     * @param value True to permit, false otherwise.
     */
    EXTENDED_ASSEMBLER_ENABLED("ExtendedAssembler", true),
    /**
     * Flag to determine whether or not program being assembled is limited to
     * using register numbers instead of names. NOTE: Its default value is
     * false and the IDE provides no means to change it!
     */
    /*
     * Setting for whether user programs limited to "bare machine" formatted basic instructions.
     * This was added 8 Aug 2006 but is fixed at false for now, due to uncertainty as to what
     * exactly constitutes "bare machine".
     *
     * @return true if only bare machine instructions allowed, false otherwise.
     */
    BARE_MACHINE_ENABLED("BareMachine", false),
    /**
     * Flag to determine whether or not a file is immediately and automatically assembled
     * upon opening. Handy when using externa editor like mipster.
     */
    /*
     * Setting for whether selected program will be automatically assembled upon opening. This
     * can be useful if user employs an external editor such as MIPSter.
     *
     * @return true if file is to be automatically assembled upon opening and false otherwise.
     */
    /*
     * Establish setting for whether a file will be automatically assembled as soon as it
     * is opened.  This is handy for those using an external text editor such as Mipster.
     * If setting changes, new setting will be written to properties file.
     *
     * @param value True to automatically assemble, false otherwise.
     */
    ASSEMBLE_ON_OPEN_ENABLED("AssembleOnOpen", false),
    /**
     * Flag to determine whether only the current editor source file (enabled false) or
     * all files in its directory (enabled true) will be assembled when assembly is selected.
     */
    /*
     * Setting for whether the assemble operation applies only to the file currently open in
     * the editor or whether it applies to all files in that file's directory (primitive project
     * capability).  If the "assemble on open" setting is set, this "assemble all" setting will
     * be applied as soon as the file is opened.
     *
     * @return true if all files are to be assembled, false if only the file open in editor.
     */
    /*
     * Establish setting for whether a file will be assembled by itself (false) or along
     * with all other files in its directory (true).  This permits multi-file programs
     * and a primitive "project" capability.  If setting changes,
     * new setting will be written to properties file.
     *
     * @param value True to assemble all, false otherwise.
     */
    ASSEMBLE_ALL_ENABLED("AssembleAll", false),
    /**
     * Default visibility of label window (symbol table).  Default only, dynamic status
     * maintained by ExecutePane
     */
    /*
     * Setting concerning whether or not to display the Labels Window -- symbol table.
     *
     * @return true if label window is to be displayed, false otherwise.
     */
    /*
     * Establish setting for whether the labels window (i.e. symbol table) will
     * be displayed as part of the Text Segment display.  If setting changes,
     * new setting will be written to properties file.
     *
     * @param value True to dispay labels window, false otherwise.
     */
    LABEL_WINDOW_VISIBILITY("LabelWindowVisibility", false),
    /**
     * Default setting for displaying addresses and values in hexidecimal in the Execute
     * pane.
     */
    /*
     * Setting for whether Addresses in the Execute pane will be displayed in hexadecimal.
     *
     * @return true if addresses are displayed in hexadecimal and false otherwise (decimal).
     */
    /*
     * Establish setting for whether addresses in the Execute pane will be displayed
     * in hexadecimal format.
     *
     * @param value True to display addresses in hexadecimal, false for decimal.
     */
    DISPLAY_ADDRESSES_IN_HEX("DisplayAddressesInHex", true),
    /*
     * Setting for whether values in the Execute pane will be displayed in hexadecimal.
     *
     * @return true if values are displayed in hexadecimal and false otherwise (decimal).
     */
    /*
     * Establish setting for whether values in the Execute pane will be displayed
     * in hexadecimal format.
     *
     * @param value True to display values in hexadecimal, false for decimal.
     */
    // DISPLAY_VALUES_IN_HEX("DisplayValuesInHex", true),
    /**
     * Flag to determine whether the currently selected exception handler source file will
     * be included in each assembly operation.
     */
    /*
     * Setting for whether the currently selected exception handler
     * (a MIPS source file) will be automatically included in each
     * assemble operation.
     *
     * @return true if exception handler is to be included in assemble, false otherwise.
     */
    /*
     * Establish setting for whether the currently selected exception handler
     * (a MIPS source file) will be automatically included in each
     * assemble operation. If setting changes, new setting will be written
     * to properties file.
     *
     * @param value True to assemble exception handler, false otherwise.
     */
    EXCEPTION_HANDLER_ENABLED("LoadExceptionHandler", false),
    /**
     * Flag to determine whether or not delayed branching is in effect at MIPS execution.
     * This means we simulate the pipeline and statement FOLLOWING a successful branch
     * is executed before branch is taken. DPS 14 June 2007.
     */
    /*
     * Setting for whether delayed branching will be applied during MIPS
     * program execution.  If enabled, the statement following a successful
     * branch will be executed and then the branch is taken!  This simulates
     * pipelining and all MIPS processors do it.  However it is confusing to
     * assembly language students so is disabled by default.  SPIM does same thing.
     *
     * @return true if delayed branching is enabled, false otherwise.
     */
    /*
     * Establish setting for whether delayed branching will be applied during
     * MIPS program execution.  If enabled, the statement following a successful
     * branch will be executed and then the branch is taken!  This simulates
     * pipelining and all MIPS processors do it.  However it is confusing to
     * assembly language students so is disabled by default.  SPIM does same thing.
     *
     * @param value True to enable delayed branching, false otherwise.
     */
    /*
     * Establish setting for whether delayed branching will be applied during
     * MIPS program execution.  This setting will NOT be written to persistent
     * store!  This method should be called only to temporarily set this
     * setting -- currently this is needed only when running MARS from the
     * command line.
     *
     * @param value True to enabled delayed branching, false otherwise.
     */
    DELAYED_BRANCHING_ENABLED("DelayedBranching", false),
    /**
     * Flag to determine whether or not the editor will display line numbers.
     */
    /*
     * Setting concerning whether or not the editor will display line numbers.
     *
     * @return true if line numbers are to be displayed, false otherwise.
     */
    /*
     * Establish setting for whether line numbers will be displayed by the
     * text editor.
     *
     * @param value True to display line numbers, false otherwise.
     */
    EDITOR_LINE_NUMBERS_DISPLAYED("EditorLineNumbersDisplayed", true),
    /**
     * Flag to determine whether or not assembler warnings are considered errors.
     */
    /*
     * Setting concerning whether or not assembler will consider warnings to be errors.
     *
     * @return true if warnings are considered errors, false otherwise.
     */
    /*
     * Establish setting for whether assembler warnings will be considered errors.
     *
     * @param value True to consider warnings to be errors, false otherwise.
     */
    WARNINGS_ARE_ERRORS("WarningsAreErrors", false),
    /**
     * Flag to determine whether or not to display and use program arguments
     */
    /*
     * Setting concerning whether or not program arguments can be entered and used.
     *
     * @return true if program arguments can be entered/used, false otherwise.
     */
    /*
     * Establish setting for whether program arguments can be ented/used.
     *
     * @param value True if program arguments can be entered/used, false otherwise.
     */
    PROGRAM_ARGUMENTS("ProgramArguments", false),
    /**
     * Flag to control whether or not highlighting is applied to data segment window
     */
    /*
     * Setting concerning whether or not highlighting is applied to Data Segment window.
     *
     * @return true if highlighting is to be applied, false otherwise.
     */
    /*
     * Establish setting for whether highlighting is to be applied to
     * Data Segment window.
     *
     * @param value True if highlighting is to be applied, false otherwise.
     */
    DATA_SEGMENT_HIGHLIGHTING("DataSegmentHighlighting", true),
    /**
     * Flag to control whether or not highlighting is applied to register windows
     */
    /*
     * Setting concerning whether or not highlighting is applied to Registers,
     * Coprocessor0, and Coprocessor1 windows.
     *
     * @return true if highlighting is to be applied, false otherwise.
     */
    /*
     * Establish setting for whether highlighting is to be applied to
     * Registers, Coprocessor0 and Coprocessor1 windows.
     *
     * @param value True if highlighting is to be applied, false otherwise.
     */
    REGISTERS_HIGHLIGHTING("RegistersHighlighting", true),
    /**
     * Flag to control whether or not assembler automatically initializes program counter to 'main's address
     */
    /*
     * Setting concerning whether or not assembler will automatically initialize
     * the program counter to address of statement labeled 'main' if defined.
     *
     * @return true if it initializes to 'main', false otherwise.
     */
    /*
     * Establish setting for whether assembler will automatically initialize
     * program counter to address of statement labeled 'main' if defined.
     *
     * @param value True if PC set to address of 'main', false otherwise.
     */
    START_AT_MAIN("StartAtMain", true),
    /**
     * Flag to control whether or not editor will highlight the line currently being edited
     */
    EDITOR_CURRENT_LINE_HIGHLIGHTING("EditorCurrentLineHighlighting", true),
    /**
     * Flag to control whether or not editor will provide popup instruction guidance while typing
     */
    POPUP_INSTRUCTION_GUIDANCE("PopupInstructionGuidance", true),
    /**
     * Flag to control whether or not simulator will use popup dialog for input syscalls
     */
    POPUP_SYSCALL_INPUT("PopupSyscallInput", false),
    /**
     * Flag to control whether or not to use generic text editor instead of language-aware styled editor
     */
    GENERIC_TEXT_EDITOR("GenericTextEditor", false),
    /**
     * Flag to control whether or not language-aware editor will use auto-indent feature
     */
    AUTO_INDENT("AutoIndent", true),
    /**
     * Flag to determine whether a program can write binary code to the text or data segment and
     * execute that code.
     */
    SELF_MODIFYING_CODE_ENABLED("SelfModifyingCode", false);

    String key;
    boolean value;
    static Settings settingsInstance;

    BooleanSetting(String key, boolean defaultValue) {
        this.key = key;
        this.value = defaultValue;
    }

    /**
     * Fetch value of a boolean setting given its identifier.
     *
     * @return corresponding boolean setting.
     */
    public boolean get() {
        return this.value;
    }

    /**
     * Set value of a boolean setting given the value.
     *
     * @param value boolean value to store
     */
    public void setTo(boolean value) {
        settingsInstance.internalSetBooleanSetting(this, value);
    }

    /**
     * Temporarily establish boolean setting.  This setting will NOT be written to persisent
     * store!  Currently this is used only when running MARS from the command line
     *
     * @param value True to enable the setting, false otherwise.
     */
    public void setBooleanSettingNonPersistent(boolean value) {
        this.value = value;
    }
}
