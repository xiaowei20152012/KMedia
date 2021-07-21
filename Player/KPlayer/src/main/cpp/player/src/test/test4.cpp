#include <iostream>

using namespace std;

void display(const double &r);
void test4();

class A
{
public:
    A(int i, int j)
    {
        x = i;
        y = j;
    }

private:
    int x, y;
};

void text4()
{
    double d(9.5);
    display(d);
    A const a(3, 4);// a is const class, can not be refreshed
}

void display(const double &r)
{
    cout << r << endl;
}
