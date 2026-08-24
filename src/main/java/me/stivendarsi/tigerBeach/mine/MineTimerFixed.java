package me.stivendarsi.tigerBeach.mine;

import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class MineTimerFixed implements Consumer<BukkitTask> {

    private final String mineId;


    public MineTimerFixed(String mineId) {
        this.mineId = mineId;
    }

    @Override
    public void accept(BukkitTask bukkitTask) {
        Mine mine = mainHandler().minesHandler().getMine(mineId);
        if (mine != null) {
            mine.fillMine();
        } else {
            bukkitTask.cancel();
        }
    }


    //
//    @Override
//    public void accept(Bukkit bukkit) {
//        Mine mine = mainHandler().minesHandler().getMine(mineId);
//        if (mine != null) {
//            mine.fillMine();
//        } else {
//            bukkit.cancel();
//        }
//    }
}