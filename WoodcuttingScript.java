import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;


// Start script

// userSelection - equipment (use-best, checkUpgrade), treeType (tree,oak,willow), location (lumbridge,draynor,varrock), mode (bank-all, burn-all-chopped-logs), bankJunk
// Equipment class - checks for best equipment available that user can equip based on WC level. Checks what axes user has in inventory&bank on start then selects best option. Else, user does not have an axe, terminate script.
// treeType class - checks what type of tree the user would like to chop (tree,oak,willow)
// Location class - checks what location the user would like to run the script at (lumbridge,draynor,varrock)
// Mode Class - checks if the user would like to bankAllChopped or burnAllChopped. User selects if they want to bank all logs, or chop logs then burn them.
// BankJunk Class - checks if the user wants to clear their inventory. This option deposits all items not being used for usage purposes into the player's bank.


// If user is not at the selected location, use webWalk to get the player there.

@ScriptManifest(author="Pugger @osbot", info = "F2P Woodcutter & Burner", logo = "", name = "F2PWoodcutterBurner", version = 1)
public class WoodcuttingScript extends Script {


    @Override
    public int onLoop() throws InterruptedException {
        /*if the inventory contains an axe and a tinder box:
 walk to behind lum castle */
        if (isPrepared()) {
            if (isBehindLumCastle()) { //if the player is behind lumbridge castle:
                chopTree();     //chop tree
                if (canLightLogs()) {
                    burnLogs();
                } else {
                    chopTree();
                }
            }
        }


        //sleep until tree is chopped down
        //if inventory full:
        //use tinderbox on log until 0 logs left
        //return to chopping log
        //else:
        // keep chopping log


        /*else if inventory does not contain axe and tinder box:
 walk to lummy castle bank
 bank everything
 withdraw 1 axe 1 tinderbox */
        return random(1000, 20000);
    }

    private void chopTree() {
        RS2Object tree = getObjects().closest(obj -> obj != null && obj.getName().equals("Tree") && getMap().canReach(obj));
        getCamera().toEntity(tree);
        if (!myPlayer().isAnimating()) {
            if (tree != null) {
                if (tree.interact("Chop down")) {
                    new ConditionalSleep(5000, 2000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return false;
                        }
                    };
                }
            }
        }
        if (inventory.isFull()) {
            burnLogs();
        }
    }


    private boolean isBehindLumCastle() {
        if (new Area(3164, 3230, 3195, 3208).contains(myPosition())) {
            return true;
        } else { // else walks to behind lum castle
            getWalking().webWalk(new Area(3164, 3230, 3195, 3208));
        }
        return false;
    }

    private boolean isPrepared() { // checks for tinderbox + axe
        if (inventory.contains("Tinderbox")) {
            return true;
        } else if (!isPrepared()) {
            getWalking().webWalk(Banks.LUMBRIDGE_UPPER); // else walk to lummy bank, deposit all withdraw axe and tinderbox
            if (Banks.LUMBRIDGE_UPPER.contains(myPlayer())) {
                try {
                    bank.open();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                {
                    if (bank.isOpen()) {
                        bank.depositAll();
                        bank.depositWornItems();
                        //withdraw axe
                        bank.withdraw("Tinderbox", 1);
                        bank.withdraw("Bronze axe", 1);
                        bank.close();
                        inventory.getItem("Bronze axe").interact("Wield");
                    }
                }
            }
        }

        {
        }
        return false;
    }

    public boolean canLightLogs() {
        if (getInventory().isFull() && (getInventory().contains("Logs")) && (!myPlayer().isAnimating())) {
            return true;
        }
        return false;
    }

    private void burnLogs() {
        if (canLightLogs()) {
            if (getInventory().isFull()) {
                if (getInventory().contains("Logs")) {
                    if (getInventory().isItemSelected()) {
                        getInventory().getItem("Logs").interact();
                        getInventory().getItem("Tinderbox").interact("Use");
                        //throw in a conditional sleep until your player is not animating
                        util.Sleep.sleepUntil(() -> (!(myPlayer().isAnimating())), 5000);
                    }
                } else {
                    boolean canLightLogs = false;
                }
            }
        }
    }
}










