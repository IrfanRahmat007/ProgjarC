#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/select.h>
#include <sys/ioctl.h>
#include <linux/sockios.h>
#include <netinet/tcp.h>

char *ip;
int port;
int num;
char *username;
char *password;
int sockcli, fd, cekHeader = 0, idx = 0;
struct sockaddr_in servaddr;
int loadsetting(char *file[])                                                // Mengambil Setting dari file
{
    password=NULL;
    username=NULL;
    int fd2=open(file,O_RDONLY);
    if(fd2<=0)
    {
        perror(strerror(errno));
        return -1;
    }
    char buff[2];
    char content[4096];
    int retval=read(fd2,buff,sizeof(buff)-1);
    //printf("%d\n",retval);
    while(retval!=0)
    {
        buff[retval]='\0';
        strcat(content,buff);
        retval=read(fd2,buff,sizeof(buff)-1);
    }
    char *temp;
    ip=strtok(content,"\r\n");                                           //Memisahkan IP,port(dikonversi),Username, dan Password
    temp=strtok(NULL,"\r\n");
    port=atoi(temp);
    username=strtok(NULL,"\r\n");
    password=strtok(NULL,"\r\n");
    close(fd2);
    return 0;
}

char * loadext(char * mime)
{
    int c=strlen(mime);
    int i;
    for(i=0;i<c;i++)
    {
        mime[i]=tolower(mime[i]);
    }
    int fd2=open("mime.txt",O_RDONLY);
    char *temp2;
    char *temp;
    if(fd2<=0)
    {
        perror(strerror(errno));
        return 'bin';
    }
    char buff[2];
    char content[8192];
    int retval=read(fd2,buff,sizeof(buff)-1);
    //printf("%d\n",retval);
    while(retval!=0)
    {
        buff[retval]='\0';
        if(buff[0]=='\n')
        {
            bzero(content,sizeof(content));
            continue;
        }
        strcat(content,buff);
        if(buff[0]=='\r');
        {
            temp=strtok(content," ");
            if(strcmp(mime,temp)==0)
            {
                temp=strtok(NULL," ");
                return temp;
            }
        }
        retval=read(fd2,buff,sizeof(buff)-1);
    }


    close(fd2);
    return 'bin';
}


int quit()      //Menutup koneksi
{
    char buf[1024];
    int retval;
    write(sockcli,"QUIT\n",sizeof("QUIT"));                            //Membuat pesan quit
    retval=read(sockcli,buf,sizeof(buf)-1);
    buf[retval]='\0';
    close(sockcli);
    sockcli=0;
    return 0;
}
int EC(char buff[])                                                      //Error Check, jika karakter pertama = minus, ada error
{
if(buff[0]=='-')
{
    printf("Error\n%s",buff);
    return -1;
}
return 0;
}
int ConnSvr()                                                           //Fungsi membuat koneksi yang sebelumnya di int main
{
    char msg[1024];
    char buf[1024], header[256];
    sockcli= socket(PF_INET,SOCK_STREAM , IPPROTO_TCP);
    printf("%d\n",sockcli);
    bzero(&servaddr, sizeof(servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(port);
    inet_aton(ip, &servaddr.sin_addr);
    int retval = connect(sockcli, (struct sockaddr *)&servaddr,sizeof(servaddr));
    retval=read(sockcli,buf,sizeof(buf)-1);
    buf[retval]='\0';
}

int login()                                                            //Fungsi Login
{
    char buf2[1024];
    bzero(buf2,sizeof(buf2));
    int retval;
    char msg[1024];
    bzero(&msg,sizeof(msg));
    strcpy(msg,"USER ");                                               //Membuat message 'USER <username>'
    strcat(msg,username);
    strcat(msg,"\n");
    write(sockcli,msg,strlen(msg));
    retval=read(sockcli,buf2,sizeof(buf2)-1);
    buf2[retval]='\0';
    EC(buf2);
    bzero(buf2,sizeof(buf2));
    strcpy(msg,"PASS ");                                                //Membuat message 'PASS <password>'
    strcat(msg,password);
    strcat(msg,"\n");
    write(sockcli,msg,strlen(msg));
    retval=read(sockcli,buf2,sizeof(buf2)-1);
    buf2[retval]='\0';
    if(EC(buf2)!=0)
    {
        return -1;
    }
    username[strlen(username)]='\0';
    return 1;
}


int FreeRBuf()                                                      //Mengosongkan read buffer
{
    int count;
    char buf[2];

    ioctl(sockcli, FIONREAD, &count);                                //Jika count <> 0, maka ada isinya
    while(count--)
    {
        read(sockcli,&buf,sizeof(buf)-1);
    }
    return 0;
}

int getSI()                                                         //Mengambil jumlah message
{
    FreeRBuf();
    char buf2[1024];
    bzero(buf2,sizeof(buf2));
    int retval;
    char msg[1024];
    strcpy(msg,"STAT\n");
    write(sockcli,msg,strlen(msg));
    retval=read(sockcli,&buf2,sizeof(buf2)-1);
    buf2[retval]='\0';
    char *temp=strtok(buf2," ");                                    //Memisahkan angka dengan delimiter spasi
    temp=strtok(NULL," ");
    return atoi(temp);
}

int getMsgList()                                                     //Mengambil List Msg
{
    FreeRBuf();
    int i;
    char buf2[2];
    char buf[4096];
    char content[4096];
    char subject[1024];
    char from[1024];
    char *temp;
    char *temp2;
    char *temp3;
    char *temp4;
    int retval,c,j;
    char msg[1024];
    for(i=1;i<=num;i++)
    {
        int h=0;
        temp=temp2=temp3=temp4=NULL;                                 //Pointer temporary untuk strtok_r
        bzero(msg,sizeof(msg));                                      //Kosongkan isi variabel
        bzero(buf,sizeof(buf));
        bzero(buf2,sizeof(buf2));
        bzero(content,sizeof(content));
        bzero(subject,sizeof(subject));
        bzero(from,sizeof(from));
        while(from[0]==0)
        {
            temp=temp2=temp3=temp4=NULL;
            bzero(msg,sizeof(msg));
            bzero(buf,sizeof(buf));
            bzero(buf2,sizeof(buf2));
            bzero(content,sizeof(content));
            bzero(subject,sizeof(subject));
            bzero(from,sizeof(from));
            strcpy(msg, "TOP ");                                      //Membuat pesan 'TOP no_pesan 0' untuk mengambil header
            sprintf(buf2,"%d %d\n",i,0);
            strcat(msg,buf2);
            //printf("%s",msg);
            write(sockcli,msg,strlen(msg));
            c=retval=read(sockcli,&buf,sizeof(buf)-1);
            buf[retval]='\0';
            strcpy(content,buf);
            while(!(buf[retval-1]=='\n' && buf[retval-2]=='\r' && buf[retval-3]=='.')) // Jika ada tanda '.\r\n', berhenti. Jika tidak, ulangi.
            {
                retval=read(sockcli,&buf,sizeof(buf)-1);
                buf[retval]='\0';
                strcat(content,buf);
            }
            bzero(buf2,sizeof(buf2));
            temp=strtok_r(&content,"\r\n",&temp4);                  //Pisahkan per baris untuk akhirnya mendapatkan subject dan pengirim
            int pending=0;
            while(temp!=NULL)
            {
                temp2=strtok(temp,":");

                h++;
                if((strcmp(temp2,"Subject")==0)||(strcmp(temp2,"subject")==0))  //Jika ketemu subjek, potong dengan delimiter ':'
                {
                    temp2=strtok(NULL,":");
                    strncpy(subject,temp2,strlen(temp2));
                }
                else if((strcmp(temp2,"From")==0)||(strcmp(temp2,"from")==0))   //Jika ketemu from, potong dengan delimiter ':'
                {
                    temp2=strtok(NULL,":");
                    strncpy(from,temp2,strlen(temp2));
                }
                temp=NULL;
                temp=strtok_r(NULL,"\r\n",&temp4);
                if(subject[0]==0 || from[0]==0)                     //Jika Subject Kosong, Ulangi
                {
                    write(sockcli,"\n",1);
                    read(sockcli,&content,sizeof(content)-1);
                }
            }
        }
        printf("\t%d. Subject:",i);                                 //Tampilkan Subjek dan pengirim per pesan
        int k=0;
        for(k=0;k<strlen(subject);k++)
        {
            printf("%c",subject[k]);
        }
        printf("\n\t   From:");
        for(k=0;k<strlen(from);k++)
        {
            printf("%c",from[k]);
        }
        printf("\n");
    }
    printf("\n");
}

int checkAttachment(int index)
{
    int total=0;
    char filename[8];
    int attachment;
    int fd,i,j,k,retval,count;
    char *temp,*temp2,*temp3,*temp4;
    char buf[2];
    char line[4096];
    temp=temp2=temp3=temp4=NULL;
    sprintf(&filename,"%d.txt",index);
    printf("%s\n",filename);
    fd=open(filename,O_RDONLY);
    retval=count=read(fd,&buf,sizeof(buf)-1);
    buf[retval]='\0';
    bzero(&line,sizeof(line));
    strcpy(&line,buf);
    while(retval!=0)
    {
        if(buf[0]=='\n')
        {
            temp=strtok_r(line,";",&temp2);
            if(temp==NULL)
            {
                continue;
            }
            if(strcmp(temp,"Content-Type: multipart/mixed")==0)
            {
                attachment=1;
                printf("Ada attachment di email ini\n");
                break;
            }
            else if(strcmp(temp,"Content-Type: multipart/alternative")==0)
            {
                attachment=1;
                printf("Email ini mengandung versi html\n");
                break;
            }
            bzero(line,sizeof(&line));
        }
        retval=read(fd,&buf,sizeof(buf)-1);
        buf[retval]='\0';
        if(buf[0]!='\n' || buf[0]!='\r')
        {
            strcat(&line,buf);
        }
    }
    close(fd);
    bzero(line,sizeof(line));
    return 0;
}

int download(int no)                                                //Fungsi Download Email
{
    FreeRBuf();                                                     //Bersihkan read buffer
    int i,fd,count;
    char buf2[2];
    char buf3[10];
    char buf[128];
    char content[1024];
    char *temp;
    char *temp2;
    char *temp3;
    char *temp4;
    int retval,c;
    char msg[1024];
    char header[1024];
    temp=temp2=temp3=temp4=NULL;
    retval=c=fd=i=count=0;
    bzero(&msg,sizeof(msg));
    bzero(&buf,sizeof(buf));
    bzero(&buf2,sizeof(buf2));
    bzero(&content,sizeof(content));
    strcpy(msg,"RETR ");                                            //Buat message 'Retr <No_email>'
    sprintf(buf2,"%d\n",no);
    strcat(msg,buf2);
    retval=write(sockcli,&msg,strlen(msg));
    count=retval=read(sockcli,&content,sizeof(content)-1);          //Hapus baris jawaban dari pengirim
    int h;
    char content2[1024];
    strcpy(content2,content);
    temp3=strtok_r(content2,".",&temp4);
    strcpy(temp4,temp3);
    temp=strtok_r(temp3," ",&temp2);
    temp=strtok_r(NULL," ",&temp2);
    c=atoi(temp);
    content[retval]='\0';
    retval=retval+sizeof(temp3);
    sprintf(buf3,"%d.txt",no);
    fd=open(buf3,O_WRONLY | O_CREAT ,0666);                          //Buat file baru
    if(fd<0)
    {
        printf("Error!");
        quit();
        exit(-1);
    }
    write(fd,&content[strlen(temp4)+3],count-strlen(temp4)-3);
    while(!(content[count-1]=='\n' && content[count-2]=='\r' && content[count-3]=='.'))
    {
        if(content[count-1]=='\n' && content[count-2]=='\r' && content[count-3]=='.')
        {
            break;
        }
        count=read(sockcli,&content,sizeof(content)-1);              //Tuliskan Konten ke file
        retval+=count;
        content[count]='\0';
        write(fd,content,count);

    }
    close(fd);
    checkAttachment(no);
}

int main()
{
    loadsetting("setting.txt");
    ConnSvr();
    int check=login();
    if(check==1)
    {
        printf("Logged in as %s\n",username);
    }
    num=getSI();
    int com;
    while(1)
    {

        printf("Select command :\n\t1. Show message counter\n\t2. Show message list\n\t3. Download a message and attachment\n\t4. Quit\n\nType a number : ");
        fflush(stdin);
        scanf("%d",&com);
        if(com==4)
        {
                quit();
                printf("Logged out\n");
                return 0;
        }
        else if(com==1)
        {
            printf("Total message : %d\n",num);
        }
        else if(com==2)
        {
            getMsgList();
        }
        else if(com==3)
        {
            printf("Enter Email number : ");
                int nomor;
                fflush(stdin);
                scanf("%d",&nomor);
                printf("No Email : %d\n",nomor);
                download(nomor);
                fflush(stdin);
        }
        else
        {
            printf("Invalid number!\n\n");
        }

    }

}
