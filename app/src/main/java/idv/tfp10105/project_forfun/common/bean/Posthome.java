package idv.tfp10105.project_forfun.common.bean;

public class Posthome {
    private Post post;
    private Member member;

    public Posthome () {

    }

    public Posthome(Post post, Member member) {
        this.post = post;
        this.member = member;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
