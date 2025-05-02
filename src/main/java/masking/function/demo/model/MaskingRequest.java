package masking.function.demo.model;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class MaskingRequest {
    // Script-level parameters
    private String createCnv;
    private String srcxf;
    private String destxf;
    private String cf;
    private String forceEditTM;
    private String delCntlFile;
    private String srcQual;
    private String destQual;
    private String valRules;
    private String unusedObj;
    private String solution;
    private String dstUsesSrc;
    private List<String> procedures;
    private String showCurrency;
    private String showAge;
    private String ageType;
    private String multiple;
    private String calendar;
    private String sampleAU;
    private String pivot;
    private String invalidDates;
    private String skippedDates;
    private String globalAgeType;
    private String globalMultiple;
    private String globalCalendar;
    private String globalSampleAU;
    private String globalPivot;
    private String globalInvalidDates;
    private String globalSkippedDates;
    private String rptError;
    private String rptSummary;
    private String rptInvalid;
    private String rptSkipped;
    private String rptMaskVerification;
    private String currencyDefault;
    private String compressFile;
    private String inclFileAttach;

    // Existing masking tables list
    private List<TableMaskingInfo> tables;
}
