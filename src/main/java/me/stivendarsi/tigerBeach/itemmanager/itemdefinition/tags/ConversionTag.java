package me.stivendarsi.tigerBeach.itemmanager.itemdefinition.tags;

import me.stivendarsi.tigerBeach.itemmanager.itemdefinition.ItemDefinitionSection;
import me.stivendarsi.tigerBeach.utility.PriceVariable;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

public class ConversionTag {
    private List<PriceVariable> convertPrice = new ArrayList<>();
    private final String nextItemDefinitionGroupName;
    private final String nextItemDefinitionId;

    private ConversionTag(String nextItemDefinitionGroupName, String nextItemDefinitionId, List<PriceVariable> convertPrice) {
        this.nextItemDefinitionGroupName = nextItemDefinitionGroupName;
        this.nextItemDefinitionId = nextItemDefinitionId;
        this.convertPrice = convertPrice;
    }

    public static Builder conversionTag() {
        return new Builder();
    }

    public List<PriceVariable> convertPrice() {
        return convertPrice;
    }

    public @Nullable ItemDefinitionSection next(){
        return mainHandler().itemDefinitionSystemHandler().getItemDefinitionSection(this.nextItemDefinitionGroupName, this.nextItemDefinitionId);
    }

    public static class Builder {
        private List<PriceVariable> convertPrice = new ArrayList<>();
        private String nextItemDefinitionGroupName;
        private String nextItemDefinitionId;

        public Builder setConvertPrice(List<PriceVariable> convertPrice) {
            this.convertPrice = convertPrice;
            return this;
        }

        public Builder setNextItemDefinitionGroupName(String nextItemDefinitionGroupName) {
            this.nextItemDefinitionGroupName = nextItemDefinitionGroupName;
            return this;
        }

        public Builder setNextItemDefinitionId(String nextItemDefinitionId) {
            this.nextItemDefinitionId = nextItemDefinitionId;
            return this;
        }

        public ConversionTag build() {
            return new ConversionTag(this.nextItemDefinitionGroupName, this.nextItemDefinitionId, convertPrice);
        }
    }
}
