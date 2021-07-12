
struct bit_field_name
{
    type member_name : width;
};

/**
 * bit_field_name 位域结构名称
 * type 位域成员类型 必须是 int, signed int or unsigned int
 * member_name 位域成员名
 * width 规定成员所占位数
 */

struct _PRCODE
{
    unsigned int code1 : 2;
    unsigned int code2 : 2;
    unsigned int code3 : 8;
};
struct _PRCODE prcode;
/*
 * 2 个 2 bits的位域
 * 1个 8 bits的位域
 * 2^8 256
 */

