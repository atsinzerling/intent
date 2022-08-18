import java.sql.Timestamp;

public class Item {
    long SKU;
    String SN;
    String PN;
    String UPC;
    String Grade;
    String Location;
    String Notes;
    String User;
    Timestamp time;
    String Images;
    String OtherRecords;
    Timestamp DateModified;
    String POnumber;
    String Specs;

//    SKU
//    SN
//    PN
//    UPC
//    Grade
//    Location
//    Notes
//    User
//    time
//    Images
//    OtherRecords
//    DateModified
//    POnumber
//    Specs

    //Strings
//    SN
//    PN
//    UPC
//    Grade
//    Location
//    Notes
//    User
//    Images
//    OtherRecords

//    2
//    3
//    4
//    5
//    6
//    7
//    8
//    9
//    10
//    11
//    12

//    SKU
//    SN
//    PN
//    UPC
//    Grade
//    Location
//    Notes
//    User
//    Images
//    OtherRecords
//    POnumber
//    Specs




    public Item(long SKU, String SN, String PN, String UPC, String grade, String location, String notes,
                String user, Timestamp time, String images, String otherRecords, Timestamp DateModified, String POnumber, String Specs) {
        this.SKU = SKU;
        this.SN = SN;
        this.PN = PN;
        this.UPC = UPC;
        Grade = grade;
        Location = location;
        Notes = notes;
        User = user;
        this.time = time;
        Images = images;
        OtherRecords = otherRecords;
        this.DateModified = DateModified;
        this.POnumber = POnumber;
        this.Specs = Specs;
    }

    public void copy(Item other){
        this.SKU = other.SKU;
        this.SN = other.SN;
        this.PN = other.PN;
        this.UPC = other.UPC;
        this.Grade = other.Grade;
        this.Location = other.Location;
        this.Notes = other.Notes;
        this.User = other.User;
        this.time = other.time;
        this.Images = other.Images;
        this.OtherRecords = other.OtherRecords;
        this.DateModified = other.DateModified;
        this.POnumber = other.POnumber;
        this.Specs = other.Specs;
    }

    public String toString(){
        return "SKU:"+SKU+" "+
            "SN:"+SN+" "+
            "PN:"+PN+" "+
            "UPC:"+UPC+" "+
            "Grade:"+Grade+" "+
            "Location:"+Location+" "+
            "Notes:"+Notes+" "+
            "User:"+User+" "+
            "time:"+time+" "+
            "Images:"+Images+" "+
            "OtherRecords:"+OtherRecords+" "+
            "DateModified:"+time;
    }

    public String excelFormat(){
        return
            (SKU)+"\t"+
            (SN==null?"":SN)+"\t"+
            (PN==null?"":PN)+"\t"+
            (UPC==null?"":UPC)+"\t"+
            (Grade==null?"":Grade)+"\t"+
            (Location==null?"":Location)+"\t"+
            (Notes==null?"":Notes.replaceAll("\t", "   ").replaceAll("\n", "   "))+"\t"+
            (User==null?"":User)+"\t"+
            (time==null?"":time)+"\t"+
            "\t"+
            (OtherRecords==null?"":OtherRecords.replaceAll("\t", "   ").replaceAll("\n", "   "))+"\t"+
            (DateModified==null?"":DateModified)+"\t"+
            (POnumber==null?"":POnumber)+"\t"+
            (Specs==null?"":Specs.replaceAll("\t", "   ").replaceAll("\n", "   "))
                +"\n"
            ;
    }

    public long getSKU() {
        return SKU;
    }

    public void setSKU(long SKU) {
        this.SKU = SKU;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getPN() {
        return PN;
    }

    public void setPN(String PN) {
        this.PN = PN;
    }

    public String getUPC() {
        return UPC;
    }

    public void setUPC(String UPC) {
        this.UPC = UPC;
    }

    public String getGrade() {
        return Grade;
    }

    public void setGrade(String grade) {
        Grade = grade;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getNotes() {
        return (Notes!=null?Notes.replaceAll("\n"," ").replaceAll("\t"," "):null);
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getImages() {
        return Images;
    }

    public void setImages(String images) {
        Images = images;
    }

    public String getOtherRecords() {
        return (OtherRecords!=null?OtherRecords.replaceAll("\n"," ").replaceAll("\t"," "):null);
    }

    public void setOtherRecords(String otherRecords) {
        OtherRecords = otherRecords;
    }

    public Timestamp getDateModified() {return DateModified;}

    public void setDateModified(Timestamp dateModified) { DateModified = dateModified; }

    public String getPOnumber() {
        return POnumber;
    }

    public void setPOnumber(String POnumber) {
        this.POnumber = POnumber;
    }

    public String getSpecs() {
        return (Specs!=null?Specs.replaceAll("\n"," ").replaceAll("\t"," "):null);
    }

    public void setSpecs(String specs) {
        Specs = specs;
    }
}
