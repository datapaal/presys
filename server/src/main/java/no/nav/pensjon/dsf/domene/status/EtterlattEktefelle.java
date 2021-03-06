package no.nav.pensjon.dsf.domene.status;

import no.nav.pensjon.presys.utils.ebcdic.annotations.Felt;
import no.nav.pensjon.presys.utils.ebcdic.annotations.PackedDecimal;
import no.nav.pensjon.presys.utils.ebcdic.annotations.Segment;
import no.nav.pensjon.presys.utils.ebcdic.annotations.UnmappedField;

/**
 * Opplysninger om etterlatt ektefelle eller familiepleier
 */
@Segment(name = "ETTEPENS", length = 62)
@UnmappedField(name = "framleggsdatoAMD", start = 32, length = 5)
@UnmappedField(name = "framleggskode", start = 37, length = 1)
@UnmappedField(name = "spt1291", start = 43, length = 2)
@UnmappedField(name = "opt1291", start = 45, length = 2)
@UnmappedField(name = "paa1291", start = 47, length = 2)
@UnmappedField(name = "paaEtter91", start = 49, length = 2)
@UnmappedField(name = "paaEtter91", start = 55, length = 7)
public class EtterlattEktefelle {
    @Felt(name = "omsorgAvdBa", start = 0, length = 1)
    private String omsorgAvdBa;

    @Felt(name = "vilkaarPar10Pkt5", start = 1, length = 1)
    private String vilkaarPar10Pkt5;

    @Felt(name = "hjemmelPar10Pkt8", start = 2, length = 1)
    private String hjemmelPar10Pkt8;

    @Felt(name = "gpBrutto", start = 3, length = 3)
    @PackedDecimal
    private int gpBrutto;

    @Felt(name = "gpNetto", start = 6, length = 3)
    @PackedDecimal
    private int gpNetto;

    @Felt(name = "st", start = 9, length = 3)
    @PackedDecimal
    private int st;

    @Felt(name = "kt", start = 12, length = 3)
    @PackedDecimal
    private int kt;

    @Felt(name = "tpBrutto", start = 15, length = 3)
    @PackedDecimal
    private int tpBrutto;

    @Felt(name = "tpNetto", start = 18, length = 3)
    @PackedDecimal
    private int tpNetto;

    @Felt(name = "sptAvd", start = 21, length = 2)
    @PackedDecimal(decimals = 2)
    private String sptAvd;

    @Felt(name = "optAvd", start = 23, length = 2)
    @PackedDecimal(decimals = 2)
    private String optAvd;

    @Felt(name = "paaAvd", start = 25, length = 2)
    @PackedDecimal
    private int paaAvd;

    @Felt(name = "tpProsent", start = 27, length = 2)
    @PackedDecimal
    private int tpProsent;

    @Felt(name = "forventet", start = 29, length = 3)
    @PackedDecimal
    private int forventet;

    @Felt(name = "stiAMD", start = 38, length = 5)
    @PackedDecimal
    private int stiAMD;

    @Felt(name = "stBrutto", start = 51, length = 3)
    @PackedDecimal
    private int stBrutto;

    @Felt(name = "fradrag2G", start = 54, length = 1)
    private String fradrag2G;

    public String getOmsorgAvdBa() { return omsorgAvdBa; }

    public void setOmsorgAvdBa(String omsorgAvdBa) { this.omsorgAvdBa = omsorgAvdBa; }

    public String getVilkaarPar10Pkt5() { return vilkaarPar10Pkt5; }

    public void setVilkaarPar10Pkt5(String vilkaarPar10Pkt5) { this.vilkaarPar10Pkt5 = vilkaarPar10Pkt5; }

    public String getHjemmelPar10Pkt8() { return hjemmelPar10Pkt8; }

    public void setHjemmelPar10Pkt8(String hjemmelPar10Pkt8) { this.hjemmelPar10Pkt8 = hjemmelPar10Pkt8; }

    public int getGpBrutto() { return gpBrutto; }

    public void setGpBrutto(int gpBrutto) { this.gpBrutto = gpBrutto; }

    public int getGpNetto() { return gpNetto; }

    public void setGpNetto(int gpNetto) { this.gpNetto = gpNetto; }

    public int getSt() { return st; }

    public void setSt(int st) { this.st = st; }

    public int getKt() { return kt; }

    public void setKt(int kt) { this.kt = kt; }

    public int getTpBrutto() { return tpBrutto; }

    public void setTpBrutto(int tpBrutto) { this.tpBrutto = tpBrutto; }

    public int getTpNetto() { return tpNetto; }

    public void setTpNetto(int tpNetto) { this.tpNetto = tpNetto; }

    public String getSptAvd() { return sptAvd; }

    public void setSptAvd(String sptAvd) { this.sptAvd = sptAvd; }

    public String getOptAvd() { return optAvd; }

    public void setOptAvd(String optAvd) { this.optAvd = optAvd; }

    public int getPaaAvd() { return paaAvd; }

    public void setPaaAvd(int paaAvd) { this.paaAvd = paaAvd; }

    public int getTpProsent() { return tpProsent; }

    public void setTpProsent(int tpProsent) { this.tpProsent = tpProsent; }

    public int getForventet() { return forventet; }

    public void setForventet(int forventet) { this.forventet = forventet; }

    public int getStiAMD() { return stiAMD; }

    public void setStiAMD(int stiAMD) { this.stiAMD = stiAMD; }

    public int getStBrutto() { return stBrutto; }

    public void setStBrutto(int stBrutto) { this.stBrutto = stBrutto; }

    public String getFradrag2G() { return fradrag2G; }

    public void setFradrag2G(String fradrag2G) { this.fradrag2G = fradrag2G; }
}
