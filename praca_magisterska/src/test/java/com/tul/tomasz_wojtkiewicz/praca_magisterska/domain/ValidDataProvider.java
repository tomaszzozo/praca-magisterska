package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import lombok.experimental.UtilityClass;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

@UtilityClass
class ValidDataProvider {

    static Stream<Arguments> provideValidEmployeeCreationData() {
        return Stream.of(
                Arguments.of("Ruggiero","Stealy","rstealy0@plala.or.jp","712568069"),
                Arguments.of("Jan","Żółcik","cbiggin1@tiny.cc","709347655"),
                Arguments.of("Żaneta","Krysiak","sbowlesworth2@de.vu","452172090"),
                Arguments.of("Waneta","Karus","wkarus3@reverbnation.com","164781938"),
                Arguments.of("Roxine","Maypother","rmaypother4@paginegialle.it","131396665"),
                Arguments.of("Clyde","Beyer","cbeyer5@dyndns.org","560599143"),
                Arguments.of("Gal","McWhorter","gmcwhorter6@noaa.gov","618709380"),
                Arguments.of("Parry","Pascoe","ppascoe7@zimbio.com","226206126"),
                Arguments.of("Teriann","Huffy","thuffy8@wunderground.com","823368512"),
                Arguments.of("Ilyse","Sandeford","isandeford9@cdc.gov","861156530"),
                Arguments.of("Neymar Jr.","Castletine","kcastletinea@pinterest.com","133750850"),
                Arguments.of("Egan","McCole","emccoleb@weather.com","100838335"),
                Arguments.of("Arny","Kullmann","akullmannc@webs.com","514350744"),
                Arguments.of("Aili","Abrahams","aabrahamsd@imgur.com","299985712"),
                Arguments.of("Mc'Carthy","Wedlake","mwedlakee@cnbc.com","924637260"),
                Arguments.of("Field","Mc'Carthy","fhoofef@army.mil","993826962"),
                Arguments.of("Emmie","Melladew","emelladewg@elpais.com","834574683"),
                Arguments.of("Hestia","Driutti","hdriuttih@seesaa.net","412560220"),
                Arguments.of("Filberto","Letty","flettyi@mac.com","576717531"),
                Arguments.of("Florry","Stenner","fstennerj@opensource.org","366559548"),
                Arguments.of("Cristionna","Wyllcocks","cwyllcocksk@mayoclinic.com","381482916"),
                Arguments.of("Mia","Cristofaro","mcristofarol@barnesandnoble.com","167367254"),
                Arguments.of("Jennifer","Gilmour","jgilmourm@flickr.com","263687055"),
                Arguments.of("Freida","Cohani","fcohanin@clickbank.net","264253983"),
                Arguments.of("Jim","Peverell","jpeverello@instagram.com","858875866"),
                Arguments.of("Nicol","Hallaways","nhallawaysp@chron.com","609913522"),
                Arguments.of("Kyle","Peffer","kpefferq@chron.com","283827061"),
                Arguments.of("Florette","Chedgey","fchedgeyr@scribd.com","167525520"),
                Arguments.of("Tallou","Chipchase","tchipchases@senate.gov","131161959"),
                Arguments.of("Rodina","Vasechkin","rvasechkint@istockphoto.com","926199617"),
                Arguments.of("Harper","Cappineer","hcappineeru@simplemachines.org","796721359"),
                Arguments.of("Clarabelle","Yukhnevich","cyukhnevichv@sphinn.com","879236404"),
                Arguments.of("Ozzy","Wrangle","owranglew@cisco.com","117719305"),
                Arguments.of("Clarie","McFarlane","cmcfarlanex@squidoo.com","471113607"),
                Arguments.of("Gabriela","Bollard","gbollardy@japanpost.jp","455882554"),
                Arguments.of("Eddie","Castanyer","ecastanyerz@liveinternet.ru","663461916"),
                Arguments.of("Noel","Avramchik","navramchik10@dyndns.org","425841005"),
                Arguments.of("Mercedes","Beament","mbeament11@jimdo.com","673486328"),
                Arguments.of("Ebba","Timewell","etimewell12@plala.or.jp","710538051"),
                Arguments.of("Currey","Haggath","chaggath13@wikimedia.org","346332214"),
                Arguments.of("Kelwin","Spellicy","kspellicy14@homestead.com","167467542"),
                Arguments.of("Nari","Simonett","nsimonett15@berkeley.edu","701322753"),
                Arguments.of("Ugo","Capeling","ucapeling16@flickr.com","934177897"),
                Arguments.of("Jocelyn","O'Halloran","johalloran17@hc360.com","632207113"),
                Arguments.of("Sheri","Sterricks","ssterricks18@github.com","833330841"),
                Arguments.of("Brant","Cortin","bcortin19@china.com.cn","690664947"),
                Arguments.of("Audrie","Spedding","aspedding1a@amazon.co.uk","260369861"),
                Arguments.of("Yasmin","Kingswell","ykingswell1b@alexa.com","203146008"),
                Arguments.of("Alyce","Geekin","ageekin1c@i2i.jp","951254209"),
                Arguments.of("Bellina","Candish","bcandish1d@dropbox.com","609772747")
        );
    }
}
