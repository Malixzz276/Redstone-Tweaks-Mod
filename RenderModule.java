package dev.redstonetweaks;

/** One toggleable module. enabled = this thing's rendering is skipped. */
public enum RenderModule {
    PISTON("piston", "Piston Rendering", "Hide piston movement animations"),
    TNT("tnt", "TNT Explosive Rendering", "Hide primed TNT and explosion particles"),
    OBSERVER("observer", "Observer Rendering", "Hide observer particles"),
    NOTE_BLOCK("note_block", "Note Block Rendering", "Hide note block particles"),
    REPEATER_COMPARATOR("repeater_comparator", "Repeater / Comparator Rendering", "Hide repeater particles"),
    TRIPWIRE("tripwire", "Tripwire Rendering", "Skip visual update when a tripwire/hook is triggered");

    public final String id;
    public final String displayName;
    public final String description;
    public boolean enabled = true;

    RenderModule(String id, String displayName, String description) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
    }
}
