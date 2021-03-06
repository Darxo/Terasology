package org.terasology.rendering.gui.widgets;

import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;

import javax.vecmath.Vector2f;

import org.lwjgl.opengl.GL11;
import org.terasology.asset.AssetManager;
import org.terasology.components.ItemComponent;
import org.terasology.world.block.BlockItemComponent;
import org.terasology.entitySystem.EntityRef;
import org.terasology.model.inventory.Icon;
import org.terasology.rendering.assets.Texture;
import org.terasology.rendering.gui.framework.UIDisplayContainer;
import org.terasology.world.block.Block;
import org.terasology.world.block.family.BlockFamily;

/**
 * Displays a little icon and item count for an item cell.
 *
 * @author Marcel Lehwald <marcel.lehwald@googlemail.com>
 */
public class UIItemCellIcon extends UIDisplayContainer {
    //entity
    private EntityRef itemEntity = EntityRef.NULL;

    //sub elements
    private final UIText itemCount;

    //layout
    private Texture terrainTex;
    private final Vector2f itemCountPosition = new Vector2f(26f, 5f);

    public UIItemCellIcon() {
        terrainTex = AssetManager.loadTexture("engine:terrain");

        itemCount = new UIText();
        itemCount.setVisible(false);
        itemCount.setPosition(itemCountPosition);

        addDisplayElement(itemCount);
    }

    @Override
    public void layout() {

    }

    @Override
    public void update() {
        ItemComponent itemComponent = itemEntity.getComponent(ItemComponent.class);
        //item count visibility
        if (itemComponent != null) {
            if (itemComponent.stackCount > 1) {
                itemCount.setVisible(true);
                itemCount.setText(Integer.toString(itemComponent.stackCount));
            } else {
                itemCount.setVisible(false);
            }
        }
    }

    @Override
    public void render() {
        ItemComponent itemComponent = itemEntity.getComponent(ItemComponent.class);
        if (itemComponent == null)
            return;

        //render icon
        if (itemComponent.icon.isEmpty()) {
            BlockItemComponent blockItem = itemEntity.getComponent(BlockItemComponent.class);
            if (blockItem != null) {
                renderBlockIcon(blockItem.blockFamily);
            }
        } else {
            Icon icon = Icon.get(itemComponent.icon);
            if (icon != null) {
                renderIcon(icon);
            }
        }

        super.render();
    }

    private void renderIcon(Icon icon) {
        glEnable(GL11.GL_DEPTH_TEST);
        glClear(GL11.GL_DEPTH_BUFFER_BIT);
        glPushMatrix();
        glTranslatef(20f, 20f, 0f);
        icon.render();
        glPopMatrix();
        glDisable(GL11.GL_DEPTH_TEST);
    }

    private void renderBlockIcon(BlockFamily blockFamily) {
        if (blockFamily == null) {
            renderIcon(Icon.get("questionmark"));
            return;
        }

        glEnable(GL11.GL_DEPTH_TEST);
        glClear(GL11.GL_DEPTH_BUFFER_BIT);
        glPushMatrix();

        glTranslatef(20f, 20f, 0f);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPushMatrix();
        glTranslatef(4f, 0f, 0f);
        GL11.glScalef(20f, 20f, 20f);
        GL11.glRotatef(170f, 1f, 0f, 0f);
        GL11.glRotatef(-16f, 0f, 1f, 0f);
        glBindTexture(GL11.GL_TEXTURE_2D, terrainTex.getId());

        Block block = blockFamily.getArchetypeBlock();
        block.renderWithLightValue(1.0f);

        GL11.glPopMatrix();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        glPopMatrix();
        glDisable(GL11.GL_DEPTH_TEST);
    }

    public EntityRef getItemEntity() {
        return itemEntity;
    }

    public void setItemEntity(EntityRef itemEntity) {
        this.itemEntity = itemEntity;
    }
}
