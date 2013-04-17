package feCapes.client;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.collect.ImmutableList;

import feCapes.FeCapes;

public class CapeGui extends GuiScreen
{
    public static final String GUIBG           = "/mods/FeCapes/textures/gui/capesbg.png";

    private static final int   xSize           = 176;
    private static final int   ySize           = 170;

    private static final int   ID_PAGE_LEFT    = 1;
    private static final int   ID_DONE_SELECT  = 2;
    private static final int   ID_PAGE_RIGHT   = 3;
    private static final int   ID_NONE         = 4;
    private static final int   ID_END_CONTROLS = 10;
    private static final int   ID_CAPES_START  = 500;

    public EntityPlayer        player;

    public List<String>        capeList;

    public int                 pageNumber;

    protected int              guiLeft;
    protected int              guiTop;

    private float              mouseX;
    private float              mouseY;

    private String             currentCape;
    private String             oldCape;

    public CapeGui(EntityPlayer player)
    {
        this.player = player;

        this.capeList = ImmutableList.copyOf(PacketHandlerClient.capes);
        this.oldCape = this.currentCape = getCapeName(player);
    }

    private static String getCapeName(EntityPlayer player)
    {
        try
        {
            int li = player.cloakUrl.lastIndexOf("/") + 1;
            return player.cloakUrl.substring(li).replaceAll(".png", "");
        }
        catch (Exception e)
        {
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        if (player == null)
            mc.displayGuiScreen(null);
        else
        {
            buttonList.clear();

            guiLeft = (width - xSize) / 2;
            guiTop = (height - ySize) / 2;

            buttonList.add(new GuiButton(ID_PAGE_LEFT, width / 2 - 6, height / 2 + 54, 20, 20, "<"));
            buttonList.add(new GuiButton(ID_PAGE_RIGHT, width / 2 + 62, height / 2 + 54, 20, 20, ">"));
            buttonList.add(new GuiButton(ID_DONE_SELECT, width / 2 + 16, height / 2 + 54, 44, 20, "Done"));

            buttonList.add(new GuiButton(ID_NONE, width / 2 - 80, height / 2 - 78, 70, 20, "None"));

            pageNumber = 0;

            if (!currentCape.equalsIgnoreCase(""))
            {
                for (int i = 0; i < capeList.size(); i++)
                {
                    String cape = capeList.get(i);
                    if (cape.equalsIgnoreCase(currentCape))
                    {
                        i -= i % 6;
                        pageNumber = i / 6;
                        break;
                    }
                }
            }
        }

        updateButtonList();
    }

    @Override
    public void onGuiClosed()
    {
        updateCape(oldCape);
    }

    @Override
    protected void actionPerformed(GuiButton btn)
    {
        if (btn.id == ID_PAGE_LEFT)
            switchPage(true);
        else if (btn.id == ID_PAGE_RIGHT)
            switchPage(false);
        else if (btn.id == ID_DONE_SELECT)
            exitAndUpdate();
        else if (btn.id == ID_NONE)
            removeCape();
        else if (btn.id >= ID_CAPES_START)
        {
            currentCape = btn.displayString;
            updateCape(btn.displayString);

            updateButtonList();
        }
        
        super.actionPerformed(btn);
    }

    private void updateCape(String cape)
    {   
        player.cloakUrl = FeCapes.conf.capeserver + cape + ".png";
    }

    public void removeCape()
    {
        currentCape = "";
        updateCape("");
        updateButtonList();
    }

    public void exitAndUpdate()
    {
        if (!currentCape.equals(oldCape)) ClientProxy.packetHandler.sendUpdate(currentCape);
        mc.displayGuiScreen(null);
    }

    public void exitWithoutUpdate()
    {
        mc.displayGuiScreen(null);
    }

    public void switchPage(boolean left)
    {
        if (left)
        {
            pageNumber--;
            if (pageNumber < 0) pageNumber = 0;
            updateButtonList();
        }
        else
        {
            pageNumber++;
            if (pageNumber * 6 >= capeList.size()) pageNumber--;
            updateButtonList();
        }
    }

    @SuppressWarnings("unchecked")
    public void updateButtonList()
    {
        for (int k1 = buttonList.size() - 1; k1 >= 0; k1--)
        {
            GuiButton btn = (GuiButton) buttonList.get(k1);

            if (btn.id > ID_CAPES_START)
                buttonList.remove(btn);
            else if (btn.id == ID_PAGE_LEFT)
            {
                if (pageNumber == 0)
                    btn.enabled = false;
                else
                    btn.enabled = true;
            }
            else if (btn.id == ID_PAGE_RIGHT)
            {
                if ((pageNumber + 1) * 6 >= capeList.size())
                    btn.enabled = false;
                else
                    btn.enabled = true;
            }
            else if (btn.id == ID_NONE) if (currentCape.equalsIgnoreCase(""))
                btn.enabled = false;
            else
                btn.enabled = true;
        }

        int button = 0;
        for (int i = pageNumber * 6; i < capeList.size() && i < (pageNumber + 1) * 6; i++)
        {
            GuiButton btn;
            String cape = capeList.get(i);

            btn = new GuiButton(ID_CAPES_START + i, width / 2 - 6, height / 2 - 78 + 22 * button, 88, 20, cape);

            if (cape.toLowerCase().equalsIgnoreCase(currentCape)) btn.enabled = false;

            buttonList.add(btn);

            button++;
            if (button == 6)
            {
                button = 0;
                break;
            }
        }

    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        drawDefaultBackground();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(GUIBG);
        int k = guiLeft;
        int l = guiTop;
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);

        for (int k1 = 0; k1 < buttonList.size(); ++k1)
        {
            GuiButton btn = (GuiButton) buttonList.get(k1);

            if (btn.id >= ID_END_CONTROLS)
            {
                int id = btn.id >= ID_CAPES_START ? btn.id - ID_CAPES_START : btn.id - ID_END_CONTROLS;
                if (!(pageNumber * 6 <= id && (pageNumber + 1) * 6 > id)) continue;
            }

            btn.drawButton(mc, par1, par2);
        }

        mouseX = par1;
        mouseY = par2;

        drawPlayerOnGui(k + 42, l + 155, 55, k + 42 - mouseX, l + 155 - 92 - mouseY);
    }

    private void drawPlayerOnGui(int par1, int par2, int par3, float par4, float par5)
    {
        if (player != null)
        {
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glPushMatrix();

            GL11.glDisable(GL11.GL_ALPHA_TEST);

            GL11.glTranslatef(par1, par2, 50.0F);
            GL11.glScalef(-par3, par3, par3);
            GL11.glRotatef(180.0F, 180.0F, 0.0F, 1.0F);
            float f2 = player.renderYawOffset;
            float f3 = player.rotationYaw;
            float f4 = player.rotationPitch;

            GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
            RenderHelper.enableStandardItemLighting();
            GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-((float) Math.atan(par5 / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);

            player.renderYawOffset = (float) Math.atan(par4 / 40.0F) * 60.0F;
            player.rotationYaw = - (float) Math.atan(par4 / 40.0F) * 60.0F;
            player.rotationPitch = -((float) Math.atan(par5 / 40.0F)) * 20.0F;
            player.rotationYawHead = - player.rotationYaw;
            GL11.glTranslatef(0.0F, player.yOffset, 0.0F);

            RenderManager.instance.playerViewY = 180.0F;
            RenderManager.instance.renderEntityWithPosYaw(player, 0.0D, 0.0D, 0.0D, 180.0F, 1.0F);
            GL11.glTranslatef(0.0F, -0.22F, 0.0F);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 255.0F * 0.8F, 255.0F * 0.8F);
            Tessellator.instance.setBrightness(240);

            player.renderYawOffset = f2;
            player.rotationYaw = f3;
            player.rotationPitch = f4;

            GL11.glEnable(GL11.GL_ALPHA_TEST);

            GL11.glPopMatrix();
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        }
    }
}
