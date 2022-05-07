import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;


@ScriptManifest(author="pugger", info = "Test", logo = "", name = "Woodcutter 9000", version = 1)
public class WoodcuttingScript extends Script {
    @Override
    public int onLoop() throws InterruptedException {

        if (shouldBank()) {
            bank();
        } else {
            cutTree(getTreeName());
        }
        return 100;
    }

    // sort by cases, check woodcutting levels.

    private boolean shouldBank() {
        return getInventory().isFull();
    }

    private void bank() throws InterruptedException {
        if (!Banks.VARROCK_WEST.contains(myPlayer())) {
            getWalking().webWalk(Banks.VARROCK_WEST);
        } else {
            if (!getBank().isOpen()) {
                getBank().open();
            } else {
                getBank().depositAllExcept(axes -> axes.getName().contains(" axe"));
            }
        }
    }

    private void cutTree(String treeName) {
        RS2Object tree = getObjects().closest(treeName);
        if (!myPlayer().isAnimating() && tree != null) {
            if (tree.interact("Chop down")) {
                new ConditionalSleep(5000) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return myPlayer().isAnimating();
                    }
                }.sleep();
            }
        }
    }

    private String getTreeName() {
        if (getSkills().getDynamic(Skill.WOODCUTTING) >= 60) {
            return "Yew";
        }
        if (getSkills().getDynamic(Skill.WOODCUTTING) >= 30) {
            return "Willow";
        }
        if (getSkills().getDynamic(Skill.WOODCUTTING) >= 15) {
            return "Oak";
        }
        else {
            return "Tree";
        }
    }
}




