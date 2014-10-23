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
int loadsetting(char *file[])
{
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
    ip=strtok(content,"\r\n");
    temp=strtok(NULL,"\r\n");
    port=atoi(temp);
    username=strtok(NULL,"\r\n");
    password=strtok(NULL,"\r\n");
    close(fd2);
    return 0;
}
void quit()
{
    char buf[1024];
    int retval;
    write(sockcli,"quit\n",sizeof("quit"));
    retval=read(sockcli,buf,sizeof(buf)-1);
    buf[retval]='\0';
    printf("Logged out\n");
    shutdown(sockcli, SHUT_RD);
    close(sockcli);
}

int EC(char buff[])
{
if(buff[0]=='-')
{
    printf("Error\n%s",buff);
    return -1;
}
return 0;
}

int login()
{
    char buf2[1024];
    bzero(buf2,sizeof(buf2));
    int retval;
    char msg[1024];
    strcpy(msg,"USER ");
    strcat(msg,username);
    strcat(msg,"\n");
    write(sockcli,msg,strlen(msg));
    retval=read(sockcli,buf2,sizeof(buf2)-1);
    buf2[retval]='\0';
    EC(buf2);
    strcpy(msg,"PASS ");
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
    printf("Logged in as %s\n",username);
}

int getSI()
{
    char buf2[1024];
    bzero(buf2,sizeof(buf2));
    int retval;
    char msg[1024];
    strcpy(msg,"STAT\n");
    write(sockcli,msg,strlen(msg));
    retval=read(sockcli,&buf2,sizeof(buf2)-1);
    buf2[retval]='\0';
//    printf("%s\n",buf2);
    char *temp=strtok(buf2," ");
    temp=strtok(NULL," ");
    return atoi(temp);
}

int getMsgList()
{

    int i;
    char buf2[2];
    char buf[128];
    char content[4096];
    char subject[1024];
    char from[1024];
    char *temp;
    char *temp2;
    char *temp3;
    char *temp4;
    int retval,c;
    char msg[1024];
    for(i=1;i<=num;i++)
    {
        int h=0;
        temp=temp2=temp3=temp4=NULL;
        bzero(msg,sizeof(msg));
        bzero(buf,sizeof(buf));
        bzero(buf2,sizeof(buf2));
        bzero(content,sizeof(content));
        bzero(subject,sizeof(subject));
        bzero(from,sizeof(from));
        while(subject[0]==0)
        {
            temp=temp2=temp3=temp4=NULL;
            bzero(msg,sizeof(msg));
            bzero(buf,sizeof(buf));
            bzero(buf2,sizeof(buf2));
            bzero(content,sizeof(content));
            bzero(subject,sizeof(subject));
            bzero(from,sizeof(from));
            strcpy(msg, "TOP ");
            sprintf(buf2,"%d %d\n",i,0);
            strcat(msg,buf2);
            //printf("%s",msg);
            write(sockcli,msg,strlen(msg));
            c=retval=read(sockcli,buf,sizeof(buf)-1);
            buf[retval]='\0';
            strcpy(content,buf);
            retval=read(sockcli,content,sizeof(content)-1);
            content[retval]='\0';
            int j;

            for(j=0;j<strlen(content);j++)
            {
                //printf("%c",content[j]);
            }
    //        printf("\n%d %d\n",strlen(content),retval);

            temp=strtok_r(&content,"\r\n",&temp4);
            int pending=0;
            while(temp!=NULL)
            {
                //printf("%s ",temp);
                temp2=strtok(temp,": ");

                h++;
                //printf("%d\n",strlen(temp2));
                //for ( ; *temp2; ++temp2) *temp2 = tolower(*temp2);
                if((strcmp(temp2,"Subject")==0)||(strcmp(temp2,"subject")==0))
                {
                    temp2=strtok(NULL,": ");
                    //printf("%s %d\n",temp2,sizeof(temp2));
                    strncpy(subject,temp2,strlen(temp2));
                }
                else if((strcmp(temp2,"From")==0)||(strcmp(temp2,"from")==0))
                {

                    temp2=strtok(NULL,": ");
                    //printf("%s\n",temp2);
                    strncpy(from,temp2,strlen(temp2));
                }
                temp=NULL;
                temp=strtok_r(NULL,"\r\n",&temp4);
                ioctl(sockcli, SIOCINQ, &pending);
                //ddprintf("%d\n",pending);
                if(subject[0]==0 || from[0]==0)
                {
                    write(sockcli,"\n",1);
                    read(sockcli,&content,sizeof(content)-1);
                }
            }
        }
        printf("\t%d. Subject: ",i);
        int k=0;
        for(k=0;k<strlen(subject);k++)
        {
            printf("%c",subject[k]);
        }
        printf(" From: ");
        for(k=0;k<strlen(from);k++)
        {
            printf("%c",from[k]);
        }
        printf("\n");
    }

}

int download(int no)
{
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
    bzero(msg,sizeof(msg));
    bzero(buf,sizeof(buf));
    bzero(buf2,sizeof(buf2));
    bzero(content,sizeof(content));
    strcpy(msg,"RETR ");
    sprintf(buf2,"%d\n",no);
    strcat(msg,buf2);
    retval=write(sockcli,msg,sizeof(msg));
    count=retval=read(sockcli,&content,sizeof(content)-1);
    char content2[1024];
    strcpy(content2,content);
    temp3=strtok_r(content2,".",&temp4);
    temp=strtok_r(temp3," ",&temp2);
    temp=strtok_r(NULL," ",&temp2);
    printf("%s %d\n",temp,strlen(temp3));
    c=atoi(temp);
    content[retval]='\0';
    retval=retval+sizeof(temp3);
    printf("%d %d\n",retval,c);
    sprintf(buf3,"%d.txt",no);
    fd=open(buf3,O_WRONLY | O_CREAT ,0666);
    if(fd<0)
    {
        printf("Error!");
        quit();
        exit(-1);
    }
    //content[count-1]!='\n' && content[count-2]!='\r' && content[count-3]!='.'
    while(retval<c)
    {
        //printf("%s\n",content);
        write(fd,content,count);
        count=read(sockcli,&content,sizeof(content)-1);
        retval+=count;
        content[count]='\0';

    }
    write(fd,content,count);
    printf("%d\n",retval);
    close(fd);
}

int main()
{
    //printf("Hello world!\n");

    loadsetting("setting.txt");
    struct sockaddr_in servaddr;
    //fd = open("index.html", O_RDWR | O_CREAT, 0666);
    char msg[1024];
    char buf[1024], header[256];
    sockcli= socket(PF_INET,SOCK_STREAM , IPPROTO_TCP);
    printf("%d\n",sockcli);
//    fcntl(sockcli, F_SETFL, O_NONBLOCK);
    bzero(&servaddr, sizeof(servaddr));
    //int status = fcntl(sockcli, F_SETFL, fcntl(sockcli, F_GETFL, 0) | O_NONBLOCK);
    //printf("%d\n",status);
    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(port);
    inet_aton(ip, &servaddr.sin_addr);
    int retval = connect(sockcli, (struct sockaddr *)&servaddr,sizeof(servaddr));
    retval=read(sockcli,buf,sizeof(buf)-1);
    buf[retval]='\0';
    printf("\r");
    EC(buf);
//    perror(strerror(errno));
    //write(sockcli, msg, strlen(msg));
    login();
    num=getSI();
    int com;
    while(1)
    {
        printf("Select command :\n\t1. Show message counter\n\t2. Show message list\n\t3. Download a message and attachment\n\t4. Quit\n\nType a number : ");
        scanf("%d",&com);
        if(com==4)
        {
            break;
        }
        switch (com)
        {
            case 1:
                printf("Total message : %d\n",num);
                break;
            case 2:
                getMsgList();
                break;
            case 3:
                printf("Enter Email number : ");
                int nomor;
                scanf("%d",&nomor);
                download(nomor);
                fflush(stdin);
                break;
            default:
                printf("Invalid number!\n\n");
                break;
        }
    }

    //printf("%s",header);
    quit();
    return 0;
}
